package com.cifpvirgendegracia.flipbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider


class LoginActivity : AppCompatActivity() {
    private lateinit var etUsuario: EditText
    private lateinit var etPass: EditText
    private val RC_SIGN_IN = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //loginTwitter()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        println(account.toString())

        etUsuario = findViewById(R.id.etUsuarioLogin)
        etPass = findViewById(R.id.etPassLogin)

        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        btnEntrar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tipo", "email")
            intent.putExtra("email", etUsuario.text.toString())
            intent.putExtra("pass", etPass.text.toString())

            if (!etPass.text.isEmpty() && !etUsuario.text.isEmpty()) {
                try {


                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(
                            etUsuario.text.toString(),
                            etPass.text.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                startActivity(intent)

                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.errorLogin),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        val btnReg = findViewById<Button>(R.id.btnRegistro)
        btnReg.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            this.finish()
            startActivity(intent)
        }
        val btnGoogle = findViewById<SignInButton>(R.id.googleLogin)
        btnGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun loginTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")

        val pendingResultTask: Task<AuthResult> =
            FirebaseAuth.getInstance().pendingAuthResult as Task<AuthResult>
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // authResult.getCredential().getAccessToken().
                    // The OAuth secret can be retrieved by calling:
                    // authResult.getCredential().getSecret().
                }
                .addOnFailureListener {
                    // Handle failure.
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }

        FirebaseAuth.getInstance()
            .startActivityForSignInWithProvider( /* activity= */this, provider.build())
            .addOnSuccessListener(
                OnSuccessListener<AuthResult?> {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // authResult.getCredential().getAccessToken().
                    // The OAuth secret can be retrieved by calling:
                    // authResult.getCredential().getSecret().
                })
            .addOnFailureListener(
                OnFailureListener {
                    // Handle failure.
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.e("Google", account.email.toString())
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", account.email.toString())
            intent.putExtra("tipo", "google")
            startActivity(intent)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Google", "signInResult:failed code=" + e.toString())
        }
    }
}