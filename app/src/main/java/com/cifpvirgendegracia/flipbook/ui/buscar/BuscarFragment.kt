package com.cifpvirgendegracia.flipbook.ui.buscar

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.cifpvirgendegracia.flipbook.R

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.FloatingSearchView
import com.cifpvirgendegracia.flipbook.adapter.LibrosAdapter
import com.cifpvirgendegracia.flipbook.model.Libro
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList


/**
 * Buscar fragment
 *
 * @constructor Create empty Buscar fragment
 */
class BuscarFragment : Fragment() {

    lateinit var mSearchView: FloatingSearchView
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var libros = ArrayList<Libro>()
    lateinit var root: View
    lateinit var myAdapter: LibrosAdapter

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
    ): View? {

        root = inflater.inflate(R.layout.fragment_buscar, container, false)
        mSearchView = root.findViewById(R.id.floating_search_view)
        busquedaVoz()
        consultaLibros()

        busquedaEscrito()

        recyclerView()


        return root
    }

    /**
     * Busqueda escrito
     *
     */
    private fun busquedaEscrito() {
        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->

            val lista = ArrayList<Libro>()
            val texto = newQuery.toLowerCase()
            if (texto != "") {
                libros.forEach {
                    if (it.autor.toLowerCase().contains(texto) || it.titulo.toLowerCase()
                            .contains(texto) || it.genero.toLowerCase().contains(texto)
                    ) {
                        lista.add(it)
                    }
                }
                myAdapter.setData(lista)
                myAdapter.notifyDataSetChanged()

            } else {
                myAdapter.setData(libros)
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Recycler view
     *
     */
    private fun recyclerView() {
        val myrv = root.findViewById(R.id.rvLibros) as RecyclerView
        myAdapter = activity?.applicationContext?.let {
            LibrosAdapter(
                it,
                libros,
                this,
                "buscarlibro"
            )
        }!!
        myrv.layoutManager = GridLayoutManager(activity?.applicationContext, 3)
        myrv.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
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
                libro?.let { libros.add(it) }
                myAdapter.notifyDataSetChanged()


            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        database.addChildEventListener(childEventListener)

    }

    /**
     * Busqueda voz
     *
     */
    private fun busquedaVoz() {
        mSearchView.setOnMenuItemClickListener {

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di un título, un autor o un género.")
            try {
                val REQ_CODE = 0
                startActivityForResult(intent, REQ_CODE)
            } catch (a: ActivityNotFoundException) {

            }
        }
    }

    /**
     * On activity result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null) {
                mSearchView.setSearchText(result[0])

                val voz = result[0].toLowerCase()
                val lista = ArrayList<Libro>()
                if (voz != "") {
                    libros.forEach {
                        if (it.autor.toLowerCase().contains(voz) || it.titulo.toLowerCase()
                                .contains(voz) || it.genero.toLowerCase().contains(voz)
                        ) {
                            lista.add(it)
                        }
                    }
                    myAdapter.setData(lista)
                    myAdapter.notifyDataSetChanged()

                }
            }
        }
    }
}