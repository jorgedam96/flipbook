package com.cifpvirgendegracia.flipbook.ui.perfil

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cifpvirgendegracia.flipbook.MainActivity
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.adapter.LibrosAdapter
import com.cifpvirgendegracia.flipbook.model.Libro
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix


/**
 * Perfil fragment
 *
 * @constructor Create empty Perfil fragment
 */
class PerfilFragment : Fragment() {
    lateinit var imgQR: ImageView
    lateinit var tvNombre: TextView
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var libros = ArrayList<Libro>()
    var librosSubidos = ArrayList<Libro>()
    lateinit var myAdapter: LibrosAdapter
    lateinit var myAdapter2: LibrosAdapter
    lateinit var root: View

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        root = inflater.inflate(R.layout.fragment_perfil, container, false)
        imgQR = root.findViewById(R.id.ivQrPerfil)
        tvNombre = root.findViewById(R.id.tvNombrePerfil)

        tvNombre.text = (activity as MainActivity).usuarioLogueado.substring(
            0,
            (activity as MainActivity).usuarioLogueado.indexOf("@")
        )
        val qr = generarQR((activity as MainActivity).usuarioLogueado)
        imgQR.setImageBitmap(qr)

        consultaLibros()
        recyclerView()
        recyclerView2()
        return root
    }

    /**
     * Recycler view
     *
     */
    private fun recyclerView() {
        val myrv = root.findViewById(R.id.recyclerRecientesPerfil) as RecyclerView
        myAdapter =
            activity?.applicationContext?.let { LibrosAdapter(it, libros, this, "perfil") }!!
        myrv.layoutManager =
            LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false);
        myrv.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

    /**
     * Recycler view2
     *
     */
    private fun recyclerView2() {
        val myrv = root.findViewById(R.id.recyclerSubidasPerfil) as RecyclerView
        myAdapter2 = activity?.applicationContext?.let {
            LibrosAdapter(
                it,
                librosSubidos,
                this,
                "perfilsubidos"
            )
        }!!
        myrv.layoutManager =
            LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false);
        myrv.adapter = myAdapter2
        myAdapter2.notifyDataSetChanged()
    }

    /**
     * Consulta libros
     *
     */
    private fun consultaLibros() {
        database = FirebaseDatabase.getInstance().getReference("libros")
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                var libro: Libro? = null
                dataSnapshot.getValue(Libro::class.java)?.let { libro = it }
                libro?.let {
                    libros.add(it)
                    libros.shuffle()
                    try {
                        if (it.usuario == (activity as MainActivity).usuarioLogueado) {
                            librosSubidos.add(it)
                            //librosSubidos.shuffle()
                        }
                    } catch (e: Exception) {
                    }

                }
                myAdapter.notifyDataSetChanged()
                myAdapter2.notifyDataSetChanged()


            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                libros.clear()
                librosSubidos.clear()
                consultaLibros()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        database.addChildEventListener(childEventListener)

    }


    /**
     * Generar q r
     *
     * @param str
     * @return
     */
    @Throws(WriterException::class)
    fun generarQR(str: String?): Bitmap? {
        val result: BitMatrix = try {
            MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE, 300, 300, null
            )
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h)
        return bitmap
    }


}