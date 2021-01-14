package com.cifpvirgendegracia.flipbook.ui.detalle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cifpvirgendegracia.flipbook.R


/**
 * Detalle usuario fragment
 *
 * @constructor Create empty Detalle usuario fragment
 */
class DetalleUsuarioFragment : Fragment() {

    /**
     * On create view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_libro, container, false)
    }


}