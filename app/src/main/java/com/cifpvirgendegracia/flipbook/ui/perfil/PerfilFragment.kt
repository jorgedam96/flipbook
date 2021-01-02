package com.cifpvirgendegracia.flipbook.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.R

class PerfilFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_perfil, container, false)
       // val textView: TextView = root.findViewById(R.id.text_notifications)

        return root
    }
}