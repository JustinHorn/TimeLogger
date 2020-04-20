package com.watch.timelogger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.MessageClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : WearableActivity() {

    private var time: Long = 0L
    private val REQ_CODE = 100

    private lateinit var speechRecognition: SpeechRecognition
    private lateinit var messageClient: MessageClient

    companion object {
        const val DATA_PATH_SEND = "/wearable_timetracker_data"
        const val DATA_PATH_RECEIVE = "/mobile_timetracker_data"
        const val DATA_TIME = "time"
        const val DATA_MESSAGE = "message"
        const val RECEIVED ="RECEIVED"
        const val LOG_TAG = "MainActivity"
        var AUDIO_PERMISSION = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOG_TAG,"onCreate")
        setContentView(R.layout.activity_main)
        speechRecognition = SpeechRecognition(progressBar, btn_speak, tv_speech,tv_recognitionState, baseContext)
        btn_speak_setOnChangedListener()
        setAutoResumeEnabled(false)
        baseContext.resources.getString(R.string.hello_world)
    }

    fun btn_speak_setOnChangedListener() {
        btn_speak.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && AUDIO_PERMISSION) {
                speechRecognition.startListening()
            } else {
                speechRecognition.stopListening()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestPermission()
    }

    private fun requestPermission() {
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = true
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            SpeechRecognition.REQUEST_RECORD_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SpeechRecognition.REQUEST_RECORD_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                AUDIO_PERMISSION = true
                btn_speak.isChecked = true
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Permission Denied!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


