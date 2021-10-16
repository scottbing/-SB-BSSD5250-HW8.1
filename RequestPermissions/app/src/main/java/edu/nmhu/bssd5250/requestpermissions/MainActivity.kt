package edu.nmhu.bssd5250.requestpermissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat

//import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var infoText:TextView
    private lateinit var permissions:Array<String>

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            infoText.text = getString(R.string.granted)
            arrayOf(
                openCamera(),
                sendSMS(),
                placeCall()
            )
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
            infoText.text = getString(R.string.denied)
        }
    }

    private val requestMultiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() ) { permissions ->
            permissions.entries.forEach {
                Log.i("DEBUG", "${it.key} = ${it.value}")
                var currText = infoText.text
                var isCam = false
                var isSms = false
                var isCal = false

                if(it.key == Manifest.permission.CAMERA){
                    currText = "$currText Camera = "
                    isCam = true
                }
                if(it.key == Manifest.permission.SEND_SMS){
                    currText = "$currText SMS = "
                    isSms = true
                }
                if(it.key == Manifest.permission.CALL_PHONE){
                    currText = "$currText Phone = "
                    isCal = true
                }

                if(it.value) {
                    currText ="$currText = Granted."
                    if(isCam){
                    }
                    if(isSms){
                    }
                    if(isCal){
                    }
                } else {
                    currText = "$currText = Denied."
                }
                infoText.text = currText
            }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
            )


//        readMotion() //TODO: delete this after proving steps work
//        readRotation() //TODO: delete this after proving steps work

        infoText = TextView(this).apply {
            hint="Click the button"
        }

        val accessButton = Button(this ).apply {
            text = "Access the Apps"
            setOnClickListener {
                permissions.forEach {
                    when {
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            arrayOf(
                                openCamera(),
                                sendSMS(),
                                placeCall()
                            )
                        }
                        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                            AlertDialog.Builder(context).apply {
                                setTitle(it.substringAfterLast(".").toString())
                                setMessage("You must allow the" + it.substringAfterLast(".").toString() + "permissions to use this feature. Ask Again?")
                                setPositiveButton("Yes") {_, _ ->
                                    requestMultiplePermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.SEND_SMS,
                                            Manifest.permission.CALL_PHONE)
                                    )
                                }
                                create()
                                show()
                            }
                        }
                        else -> {
                            // You can directly ask for the permission.
                            // The registered Activity ResultCallback gets the result of this request.
                            requestMultiplePermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE)
                            )
                        }
                    }

                }
                
            }
            
        }
        val mainLayout = LinearLayoutCompat(this).apply {
            orientation = LinearLayoutCompat.VERTICAL
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT)
            addView(infoText)
            addView(accessButton)
        }
        setContentView(mainLayout)
    }


    private fun openCamera(){
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(cameraIntent)
    }

    private fun sendSMS(){
        val uri = Uri.parse("smsto:sbing@live.nmhu.edu")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", "A message from SMS...")
        startActivity(smsIntent)
    }

    private fun placeCall(){
        val callIntent = Intent(Intent.ACTION_DIAL)
        startActivity(callIntent)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }
}