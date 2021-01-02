package com.cifpvirgendegracia.flipbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class RegistroActivity : AppCompatActivity() {
    lateinit var etUsuario: EditText
    lateinit var etPass: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        etUsuario = findViewById(R.id.etUsuarioRegistro)
        etPass = findViewById(R.id.etPassRegistro)

        val btnAtras = findViewById<Button>(R.id.btnAtras)
        btnAtras.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            this.finish()
            startActivity(intent)

        }

        val btnReg = findViewById<Button>(R.id.btnRegistrasrse)
        btnReg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            if (!etPass.text.isEmpty() && !etUsuario.text.isEmpty()) {
                try {


                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        etUsuario.text.toString(),
                        etPass.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(intent)
                            this.finish()

                        } else {
                            android.widget.Toast.makeText(
                                this, getString(R.string.errorRegistro)+" "+it.exception.toString(),
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}