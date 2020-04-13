package com.watch.timetracker

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import com.google.android.gms.wearable.DataMap

import java.util.*


class SpeechRecognition() : RecognitionListener {

    val LOG_TAG = "SpeechRecognition"

    lateinit var speech: SpeechRecognizer
    lateinit var recognizerIntent: Intent

    private lateinit var progressBar: ProgressBar
    private lateinit var toggleButton: ToggleButton
    private lateinit var returnedText: TextView
    private lateinit var recognitionState: TextView

    private lateinit var context: Context

    constructor(
        progressBar: ProgressBar,
        toggleButton: ToggleButton,
        returnedText: TextView,
        recognitionState: TextView,
        context: Context
    ) : this() {
        this.progressBar = progressBar
        this.toggleButton = toggleButton
        this.returnedText = returnedText
        this.recognitionState = recognitionState
        this.context = context
        initSpeech()
        initRecognizerIntent()
    }

    private fun initSpeech() {
        speech = SpeechRecognizer.createSpeechRecognizer(context)
        Log.i(
            LOG_TAG,
            "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(context)
        )
        speech.setRecognitionListener(this)
    }

    private fun initRecognizerIntent() {
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES,
            arrayListOf("en", Locale.getDefault().toLanguageTag())
        )
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 0)
    }

    fun startListening() {
        progressBar.visibility = View.VISIBLE
        progressBar.isIndeterminate = true
        recognitionState.text ="turning ready for speech"
        speech.startListening(recognizerIntent)
    }

    fun stopListening()  {
        speech.stopListening()
        toggleButton.isChecked = false
        progressBar.isIndeterminate = false
        progressBar.visibility = View.INVISIBLE
    }

    fun onStop() {
        speech.destroy()
        Log.i(LOG_TAG, "destroy")
    }

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech")
        recognitionState.text ="listening"
        progressBar.isIndeterminate = false
        progressBar.max = 10
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: $buffer")
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
        recognitionState.text ="converting speech"
        progressBar.isIndeterminate = true
        toggleButton.isChecked = false
    }

    override fun onError(errorCode: Int) {
        val errorMessage =
            getErrorText(
                errorCode
            )
        Log.d(LOG_TAG, "FAILED $errorMessage")
        returnedText.text = errorMessage
        toggleButton.isChecked = false
        recognitionState.text = "done"
    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }

    override fun onPartialResults(arg0: Bundle) {
        Log.i(LOG_TAG, "onPartialResults")
    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
        recognitionState.text = "waiting for speech"
    }

    override fun onResults(results: Bundle) {
        Log.i(LOG_TAG, "onResults")
        recognitionState.text = "done"
        val matches = results
            .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        for (result in matches){ text += result + "\n"}
        //toggleButton.isChecked = false
        returnedText.text = text
        sendData(text.split("\n")[0])
    }

    val entry_format = SimpleDateFormat("HH:mm")


    private fun sendData(text: String) {
        var message =  entry_format.format(Date().time)+" "+text+ "\n"
        SendToDataLayerThread(message, context).start();
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: $rmsdB")
        progressBar.progress = rmsdB.toInt()
    }

    companion object {
        const val REQUEST_RECORD_PERMISSION = 100
        fun getErrorText(errorCode: Int): String {
            return when (errorCode) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "error from server"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Didn't understand, please try again."
            }
        }
    }
}