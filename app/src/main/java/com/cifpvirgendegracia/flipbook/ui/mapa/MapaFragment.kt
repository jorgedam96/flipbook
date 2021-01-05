package com.cifpvirgendegracia.flipbook.ui.mapa

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cifpvirgendegracia.flipbook.R
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.ui.detalle.DetalleLibroFragment
import com.cifpvirgendegracia.flipbook.util.Utilidades
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MapaFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "LOG"
    private lateinit var mMap: GoogleMap
    private lateinit var root: View
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var libros = ArrayList<Libro>()
    var mapMarcadoresLibros: HashMap<Marker, Libro> = HashMap()
    var mapMarcadoresPosicion: HashMap<String, LatLng> = HashMap()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_mapa, container, false)
        this.root = root
        val mapFragment =
            childFragmentManager.fragments[0] as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)

        database = FirebaseDatabase.getInstance().getReference("libros")
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        return root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        verMarcadores()
        cargarLoc(mMap)

    }

    private fun verMarcadores() {
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                var libro: Libro? = null
                dataSnapshot.getValue(Libro::class.java)?.let { libro = it }
                libro?.let { libros.add(it) }

                Log.e("OBJ", libros.toString())

                var marcador = mMap.addMarker(libro?.localizacion?.latitud?.toDouble()?.let {
                    LatLng(
                        it, libro?.localizacion?.longitud?.toDouble()!!
                    )
                }?.let {
                    MarkerOptions()
                        .position(it)
                        .title(libro?.titulo)
                        .snippet("Género: " + libro?.genero + ", Estado: " + libro?.estado)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                Utilidades.StringToBitMap(libro?.foto)?.let { it1 ->
                                    Utilidades.resize(it1, 300, 300)
                                })
                        )
                })

                marcador.isDraggable = true
                libro?.let { mapMarcadoresLibros.put(marcador, it) }
                mapMarcadoresPosicion.put(marcador.id, marcador.position)

                mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                    override fun onMarkerDragStart(marker: Marker) {
                        //Ver detalles del libro.
                        val nextFrag = DetalleLibroFragment(mapMarcadoresLibros.get(marker))
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.map, nextFrag, "findThisFragment")
                            .addToBackStack(null)
                            .commit()

                    }

                    override fun onMarkerDragEnd(marker: Marker) {
                        //TODO volver a poner el marcador en su sitio
                        marker.position = mapMarcadoresPosicion.get(marker.id)
                    }

                    override fun onMarkerDrag(marker: Marker) {}
                })

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(
                    context, "Failed to load Libros.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        database.addChildEventListener(childEventListener)
    }


    private fun cargarLoc(mMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                root.context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                root.context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {

                    val preferencias = activity?.getSharedPreferences("loc", Context.MODE_PRIVATE)
                    val editor = preferencias?.edit()
                    editor?.putString("lat", location.latitude.toString())
                    editor?.putString("lon", location.longitude.toString())
                    editor?.apply()
                } else {
                    Toast.makeText(
                        root.context,
                        getString(R.string.enciendagps),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent)
                }
            }
    }


}