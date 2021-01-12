package com.cifpvirgendegracia.flipbook.ui.detalle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.util.Utilidades
import com.google.firebase.database.*
import java.lang.Exception


class DetalleLibroFragment(val libro: Libro?, val vista: String) : Fragment() {
    lateinit var ratingBar: RatingBar
    lateinit var root: View
    lateinit var tvTitulo: TextView
    lateinit var tvAutor: TextView
    lateinit var tvEstado: TextView
    lateinit var tvDuenio: TextView
    lateinit var ivFoto: ImageView
    lateinit var ivZoom: ImageView
    lateinit var btnReservar: Button
    lateinit var btnBorrar: Button
    lateinit var listview: ListView
    lateinit var comentarios: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_detalle_libro, container, false)

        ratingBar = root.findViewById(R.id.ratingBar)
        listview = root.findViewById(R.id.listViewComentariosLibroDetalle)
        tvTitulo = root.findViewById(R.id.tituloDetalleLibro)
        tvAutor = root.findViewById(R.id.autorDetalleLibro)
        tvEstado = root.findViewById(R.id.estadoDetalleLibro)
        tvDuenio = root.findViewById(R.id.duenioDetalleLibro)
        ivFoto = root.findViewById(R.id.ivFotoDetalleLibro)
        ivZoom = root.findViewById(R.id.zoomDetalle)
        btnReservar = root.findViewById(R.id.btnReservar)
        btnBorrar = root.findViewById(R.id.btnBorrarLibro)

        if (vista == "perfilsubidos") {
            btnReservar.visibility = View.GONE
            btnBorrar.visibility = View.VISIBLE

            btnBorrar.setOnClickListener {
                val dialogClickListener =
                    DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                if (libro != null) {
                                    borrarLibro(libro.id)
                                }
                                activity?.supportFragmentManager?.beginTransaction()?.remove(this)
                                    ?.commit()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                            }
                        }
                    }

                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setCancelable(false)
                    .setMessage("¿Estás seguro de que quieres borrar el libro?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show().window?.setBackgroundDrawableResource(R.color.azul)

            }
        }

        ivFoto.setOnClickListener {
            Log.e("clic", "foto")

            ivZoom.visibility = View.VISIBLE
            ivZoom.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(null)

        }
        ivZoom.setOnClickListener {
            Log.e("clic", "zoom")
            ivZoom.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ivZoom.visibility = View.GONE
                    }
                })
        }

        if (libro != null) {
            tvTitulo.text = libro.titulo
            tvAutor.text = libro.autor
            tvEstado.text = libro.estado
            tvDuenio.text = libro.usuario.substring(0, libro.usuario.indexOf("@"))
            ivFoto.setImageBitmap(Utilidades.StringToBitMap(libro.foto))
            ivZoom.setImageBitmap(Utilidades.StringToBitMap(libro.foto))
        }

        ratingBar.rating = (Math.random() * 5 + 1).toFloat()
        ratingBar.setIsIndicator(true)

        comentarios = ArrayList()



        comentarios.add("Jorgedam96: Muy bueno, recomendable")
        comentarios.add("Alberto2000: no esta mal ")
        comentarios.add("Javier_99: muy largo")
        comentarios.add("Carlos44: recomendable")
        comentarios.add("Ricardo23: no me ha gustado nada")
        comentarios.add("Fatima3365: tapa blanda, buen libro")
        comentarios.add("Alba_90: me leeré el siguiente")
        comentarios.add("Jorgedam96: Muy bueno, recomendable")
        comentarios.add("Alberto2000: no esta mal ")
        comentarios.add("Javier_99: muy largo")
        comentarios.add("Carlos44: recomendable")
        comentarios.add("Ricardo23: no me ha gustado nada")
        comentarios.add("Fatima3365: tapa blanda, buen libro")
        comentarios.add("Alba_90: me leeré el siguiente")

        comentarios.shuffle()

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(root.context, android.R.layout.simple_list_item_1, comentarios)
        listview.adapter = adapter


        return root
    }

    private fun borrarLibro(id: String) {


        try {
            libro?.let {
                FirebaseDatabase.getInstance().reference
                    .child("libros").child(it.id)
            }?.removeValue()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}