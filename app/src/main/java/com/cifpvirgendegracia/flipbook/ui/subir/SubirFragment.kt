package com.cifpvirgendegracia.flipbook.ui.subir

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.MainActivity
import com.cifpvirgendegracia.flipbook.R
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

class SubirFragment : Fragment() {

    private lateinit var root: View
    private lateinit var etISBN: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_subir, container, false)
        this.root = root;

        etISBN = root.findViewById(R.id.etIsbnSubir)
        val btnQR = root.findViewById<Button>(R.id.btnEscanearSubir)
        btnQR.setOnClickListener {
            scanQRCode()
        }

        return root
    }

    private fun scanQRCode() {

        val integrator = IntentIntegrator(activity).apply {
            captureActivity = CaptureActivity::class.java

            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)

            //setPrompt(getString(R.string.escaneando))
        }

        integrator.initiateScan()

        var mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {
                Log.i("HILO", "QR")
                val preferencias = activity?.getSharedPreferences("datos", Context.MODE_PRIVATE)
                if (preferencias?.getString("qr",null) != null) {
                    etISBN.setText(preferencias.getString("qr", ""))
                    val preferencias =  activity?.getSharedPreferences("datos", Context.MODE_PRIVATE)
                    val editor = preferencias?.edit()
                    editor?.remove("qr")
                    editor?.apply()
                } else {
                    mainHandler.postDelayed(this, 300)
                }
            }
        })
    }


}