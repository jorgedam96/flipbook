package com.cifpvirgendegracia.flipbook.ui.subir

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.MainActivity
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.model.Localizacion
import com.cifpvirgendegracia.flipbook.util.Utilidades
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import java.io.ByteArrayOutputStream


class SubirFragment : Fragment() {

    private lateinit var marcador: Marker
    private var fotoCambiada: Boolean = false
    private lateinit var root: View
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etGenero: TextInputEditText
    private lateinit var etAutor: TextInputEditText
    private lateinit var etISBN: TextInputEditText
    lateinit var spinner: Spinner
    lateinit var btnQR: Button
    lateinit var btnSubir: Button
    lateinit var btnFoto: ImageView
    lateinit var estados: Array<String>
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_subir, container, false)
        this.root = root
        etISBN = root.findViewById(R.id.etIsbnSubir)
        etTitulo = root.findViewById(R.id.etTituloSubir)
        etGenero = root.findViewById(R.id.etGeneroSubir)
        etAutor = root.findViewById(R.id.etAutorSubir)
        spinner = root.findViewById<Spinner>(R.id.spinnerEstadoSubir)
        btnQR = root.findViewById<Button>(R.id.btnEscanearSubir)
        btnSubir = root.findViewById<Button>(R.id.btnSubirLibSubir)
        btnFoto = root.findViewById<ImageView>(R.id.ivFotoSubir)
        estados = resources.getStringArray(R.array.estados)
        database = FirebaseDatabase.getInstance().getReference("libros");
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        spinner()
        listeners()


        return root
    }

    private fun listeners() {
        btnQR.setOnClickListener {
            escanerQR()
        }
        btnFoto.setOnClickListener {
            //if system os is Marshmallow or Above, we need to request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(root.context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    ActivityCompat.checkSelfPermission(
                        root.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    //permission already granted
                    abrirCamara()
                }
            } else {
                //system os is < marshmallow
                abrirCamara()
            }
        }
        btnSubir.setOnClickListener {
            var loc = abrirDialogoMapa()

        }
    }

    private fun subir() {
        if (fotoCambiada && !etTitulo.text.toString()
                .isEmpty() && !etISBN.text.toString().isEmpty() && !etAutor.text.toString()
                .isEmpty() && !etGenero.text.toString()
                .isEmpty() && !spinner.selectedItem.toString().isEmpty()
        ) {
            var d = btnFoto.drawable

            val bitmap = (d as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitmapdata: ByteArray = stream.toByteArray()


            val loc = Localizacion(
                database.push().key.toString(),
                marcador.position.latitude.toString(),
                marcador.position.longitude.toString()
            )
            val libro =
                Libro(
                    database.push().key.toString(),
                    etTitulo.text.toString(),
                    etISBN.text.toString(),
                    etAutor.text.toString(),
                    etGenero.text.toString(),
                    Utilidades.BitMapToString(bitmap),
                    spinner.selectedItem.toString(), (activity as MainActivity).usuarioLogueado,
                    loc
                )
            database.child(libro.id).setValue(libro).addOnSuccessListener {
                Toast.makeText(activity, "Publicación subida", Toast.LENGTH_SHORT).show()
                btnFoto.setImageDrawable(activity?.getDrawable(R.drawable.ic_add_foto))
                etTitulo.setText("")
                etISBN.setText("")
                etAutor.setText("")
                etGenero.setText("")
                spinner.setSelection(0)
            }
        } else {
            Toast.makeText(
                activity,
                "No ha sido posible subir la publicación",
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    @SuppressLint("MissingPermission")
    private fun abrirDialogoMapa() {
        Toast.makeText(
            activity,
            "Mantén pulsado el marcador y arrástralo a la ubicación deseada.",
            Toast.LENGTH_SHORT
        ).show()
        val dialog = Dialog(activity as Activity)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        /////make map clear
        /////make map clear
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        dialog.setContentView(R.layout.dialog_map) ////your custom content


        val mMapView = dialog.findViewById<View>(R.id.mapViewDialog) as MapView
        MapsInitializer.initialize(activity)

        mMapView.onCreate(dialog.onSaveInstanceState())
        mMapView.onResume()


        mMapView.getMapAsync { googleMap ->
            val preferencias = activity?.getSharedPreferences("loc", Context.MODE_PRIVATE)
            if (preferencias?.getString("lon", null) != null && preferencias.getString(
                    "lat",
                    null
                ) != null
            ) {
                val posisiabsen = LatLng(
                    preferencias.getString("lat", null)!!.toDouble(),
                    preferencias.getString("lon", null)!!.toDouble()
                )
                googleMap.setOnMarkerDragListener(object : OnMarkerDragListener {
                    override fun onMarkerDragStart(marker: Marker) {}
                    override fun onMarkerDragEnd(marker: Marker) {
                        marcador = marker
                    }

                    override fun onMarkerDrag(marker: Marker) {}
                })
                googleMap.isMyLocationEnabled = true

                marcador = googleMap.addMarker(
                    MarkerOptions().position(posisiabsen).title("Ubicación para el libro")
                        .draggable(true)
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen))
                googleMap.uiSettings.isZoomControlsEnabled = true

            } else {
            }

        }


        val dialogButton = dialog.findViewById<View>(R.id.btn_dialog) as Button

        dialogButton.setOnClickListener {
            subir()
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun abrirCamara() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = root.context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    private fun spinner() {
        if (spinner != null) {
            val adapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_dropdown_item_1line, estados
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    private fun escanerQR() {

        val integrator = IntentIntegrator(activity).apply {
            captureActivity = CaptureActivity::class.java
            setBeepEnabled(false)
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        }

        integrator.initiateScan()

        var mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                Log.i("HILO", "QR")
                val preferencias = activity?.getSharedPreferences("datos", Context.MODE_PRIVATE)
                if (preferencias?.getString("qr", null) != null) {
                    etISBN.setText(preferencias.getString("qr", ""))
                    val preferencias =
                        activity?.getSharedPreferences("datos", Context.MODE_PRIVATE)
                    val editor = preferencias?.edit()
                    editor?.remove("qr")
                    editor?.apply()
                } else {
                    mainHandler.postDelayed(this, 300)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view

            btnFoto.setImageURI(image_uri)
            btnFoto.setImageDrawable(redimensionar(btnFoto.drawable))
            fotoCambiada = true
        }
    }

    private fun redimensionar(image: Drawable): Drawable? {
        val b = (image as BitmapDrawable).bitmap
        val bitmapResized = Utilidades.resize(b, 500, 500)
        return BitmapDrawable(resources, bitmapResized)
    }
}