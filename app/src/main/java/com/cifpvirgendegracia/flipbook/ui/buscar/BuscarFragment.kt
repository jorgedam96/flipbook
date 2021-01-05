package com.cifpvirgendegracia.flipbook.ui.buscar

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import com.cifpvirgendegracia.flipbook.R

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.FloatingSearchView
import com.cifpvirgendegracia.flipbook.adapter.RecyclerViewAdapter
import com.cifpvirgendegracia.flipbook.model.Libro
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class BuscarFragment : Fragment() {

    lateinit var mSearchView: FloatingSearchView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null
    var libros = ArrayList<Libro>()
    lateinit var root: View
    lateinit var myAdapter: RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_buscar, container, false)
        mSearchView = root.findViewById(R.id.floating_search_view)
        busquedaVoz()
        consultaLibros()

        mSearchView.setOnQueryChangeListener { oldQuery, newQuery ->

            Log.e("QUERY", newQuery)
        }

        recyclerView()


        return root
    }

    private fun recyclerView() {
        val myrv = root.findViewById(R.id.rvLibros) as RecyclerView
         myAdapter = activity?.applicationContext?.let { RecyclerViewAdapter(it, libros) }!!
        myrv.layoutManager = GridLayoutManager(activity?.applicationContext, 3)
        myrv.adapter = myAdapter
        myAdapter.notifyDataSetChanged()
    }

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
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addChildEventListener(childEventListener)

    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val result = data
                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null) {
                mSearchView.setSearchText(result[0])
                //TODO buscar libros con ese texto
            }
        }
    }
}