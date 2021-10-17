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

    private lateinit var infoText:TextView
    private lateinit var permissions:Array<String>
    private lateinit var funs:Array<Unit>


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
                    if(isCam) openCamera()
                    if(isSms) sendSMS()
                    if(isCal) makeCall()
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

        this.funs = arrayOf : Unit(
            this::openCamera,
            this::sendSMS,
            this::makeCall
        )

        infoText = TextView(this).apply {
            hint="Click the button"
        }

        val accessButton = Button(this ).apply {
            text = context.getString(R.string.access_to_apps)
            setOnClickListener {
                permissions.forEach {
                    when {
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            openCamera()
                        }
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.SEND_SMS
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            sendSMS()
                        }
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            makeCall()
                        }
                        shouldShowRequestPermissionRationale(it) -> {
                            AlertDialog.Builder(context).apply {
                                setTitle(it.substringAfterLast("."))
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

    private fun makeCall(){
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