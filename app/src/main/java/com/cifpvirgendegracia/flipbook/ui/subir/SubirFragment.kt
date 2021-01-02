package com.cifpvirgendegracia.flipbook.ui.subir

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.cifpvirgendegracia.flipbook.R
import com.cifpvirgendegracia.flipbook.model.Libro
import com.cifpvirgendegracia.flipbook.model.Localizacion
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import java.io.ByteArrayOutputStream


class SubirFragment : Fragment() {

    private var fotoCambiada: Boolean = false
    private lateinit var root: View
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etGenero: TextInputEditText
    private lateinit var etAutor: TextInputEditText
    private lateinit var etISBN: TextInputEditText
    lateinit var spinner: Spinner
    lateinit var btnQR: Button
    lateinit var btnSubir: Button
    lateinit var btnFoto: ImageView
    lateinit var estados: Array<String>
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001
    var image_uri: Uri? = null
    private lateinit var database: DatabaseReference
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_subir, container, false)
        this.root = root
        etISBN = root.findViewById(R.id.etIsbnSubir)
        etTitulo = root.findViewById(R.id.etTituloSubir)
        etGenero = root.findViewById(R.id.etGeneroSubir)
        etAutor = root.findViewById(R.id.etAutorSubir)
        spinner = root.findViewById<Spinner>(R.id.spinnerEstadoSubir)
        btnQR = root.findViewById<Button>(R.id.btnEscanearSubir)
        btnSubir = root.findViewById<Button>(R.id.btnSubirLibSubir)
        btnFoto = root.findViewById<ImageView>(R.id.ivFotoSubir)
        estados = resources.getStringArray(R.array.estados)
        database = FirebaseDatabase.getInstance().getReference("libros");
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        spinner()
        listeners()


        return root
    }

    private fun listeners() {
        btnQR.setOnClickListener {
            scanQRCode()
        }
        btnFoto.setOnClickListener {
            //if system os is Marshmallow or Above, we need to request runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(root.context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    ActivityCompat.checkSelfPermission(
                        root.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    //permission already granted
                    openCamera()
                }
            } else {
                //system os is < marshmallow
                openCamera()
            }
        }

        btnSubir.setOnClickListener {

            if (fotoCambiada && !etTitulo.text.toString()
                    .isEmpty() && !etISBN.text.toString().isEmpty() && !etAutor.text.toString()
                    .isEmpty() && !etGenero.text.toString()
                    .isEmpty() && !spinner.selectedItem.toString().isEmpty()
            ) {
                var d = btnFoto.drawable

                val bitmap = (d as BitmapDrawable).bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bitmapdata: ByteArray = stream.toByteArray()


                val loc = Localizacion(database.push().key.toString(), "lat", "lon")
                val libro =
                    Libro(
                        database.push().key.toString(),
                        etTitulo.text.toString(),
                        etISBN.text.toString(),
                        etAutor.text.toString(),
                        etGenero.text.toString(),
                        String(bitmapdata),
                        spinner.selectedItem.toString(), loc
                    )
                database.child(libro.id).setValue(libro).addOnSuccessListener {
                    Toast.makeText(activity, "Publicación subida", Toast.LENGTH_SHORT).show()
                    btnFoto.setImageDrawable(activity?.getDrawable(R.drawable.ic_add_foto))
                    etTitulo.setText("")
                    etISBN.setText("")
                    etAutor.setText("")
                    etGenero.setText("")
                    spinner.setSelection(0)
                }
            } else {
                Toast.makeText(
                    activity,
                    "No ha sido posible subir la publicación",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

    }


    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = root.context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    private fun spinner() {
        if (spinner != null) {
            val adapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_dropdown_item_1line, estados
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
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
                if (preferencias?.getString("qr", null) != null) {
                    etISBN.setText(preferencias.getString("qr", ""))
                    val preferencias =
                        activity?.getSharedPreferences("datos", Context.MODE_PRIVATE)
                    val editor = preferencias?.edit()
                    editor?.remove("qr")
                    editor?.apply()
                } else {
                    mainHandler.postDelayed(this, 300)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK) {
            //set image captured to image view

            btnFoto.setImageURI(image_uri)
            btnFoto.setImageDrawable(resize(btnFoto.drawable))
            fotoCambiada = true
        }
    }

    private fun resize(image: Drawable): Drawable? {
        val b = (image as BitmapDrawable).bitmap
        val bitmapResized = Bitmap.createScaledBitmap(b, 500, 500, false)
        return BitmapDrawable(resources, bitmapResized)
    }
}