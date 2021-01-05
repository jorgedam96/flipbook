package com.cifpvirgendegracia.flipbook.ui.detalle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro


class DetalleLibroFragment(val libro: Libro?) : Fragment() {
    lateinit var ratingBar: RatingBar
    lateinit var root: View
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


        ratingBar.rating = (Math.random() * 5 + 1).toFloat()

        comentarios = ArrayList()


        if (libro != null) {
            comentarios.add(libro.titulo)
        }
        comentarios.add("Jorge Segade: Muy bueno, recomendable")
        comentarios.add("Jorge Segade: Muy bueno, recomendable")
        comentarios.add("Jorge Segade: Muy bueno, recomendable")
        comentarios.add("Jorge Segade: Muy bueno, recomendable")

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(root.context, android.R.layout.simple_list_item_1, comentarios)
        listview.adapter = adapter


        return root
    }


}