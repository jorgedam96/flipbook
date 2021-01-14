package com.cifpvirgendegracia.flipbook.ui.usuarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.adapter.LibrosAdapter
import com.cifpvirgendegracia.flipbook.adapter.UsuariosAdapter
import com.cifpvirgendegracia.flipbook.model.Usuario
import java.util.ArrayList


/**
 * Usuarios fragment
 *
 * @constructor Create empty Usuarios fragment
 */
class UsuariosFragment : Fragment() {

    lateinit var myAdapter: UsuariosAdapter

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

        val root = inflater.inflate(R.layout.fragment_usuarios, container, false)
        val usuarios = ArrayList<Usuario>()
        usuarios.add(Usuario("", "Jorgedam96", "", "", "", ""))
        usuarios.add(Usuario("", "Alberto2000", "", "", "", ""))
        usuarios.add(Usuario("", "Javier_99", "", "", "", ""))
        usuarios.add(Usuario("", "Carlos44", "", "", "", ""))
        usuarios.add(Usuario("", "Ricardo23", "", "", "", ""))
        usuarios.add(Usuario("", "Fatima3365", "", "", "", ""))
        usuarios.add(Usuario("", "Alba_90", "", "", "", ""))


        val myrv = root.findViewById(R.id.rvUsuarios) as RecyclerView
        myAdapter = activity?.applicationContext?.let {

            UsuariosAdapter(
                it,
                usuarios,
                this
            )
        }!!
        myrv.layoutManager = LinearLayoutManager(root.context,LinearLayoutManager.VERTICAL,false)
        myrv.adapter = myAdapter
        myAdapter.notifyDataSetChanged()

        return root
    }
}