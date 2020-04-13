package com.watch.timetracker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.wearable.DataMap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Duration
import java.util.*


class MainActivity : AppCompatActivity() {


    private  val REQ_CODE = 100
    private var check = true
    private var time:Long= 0L

    companion object {
        const val DATA_PATH_SEND = "/wearable_data"
        const val DATA_MESSAGE = "message"
        var count = 0
        lateinit var FILE_PATH:File
        val day_format = SimpleDateFormat("E dd.MM.yyyy")
        val entry_format = SimpleDateFormat("HH:mm")
        var day = day_format.format(Date().time)
        lateinit var et_entries:EditText //basicly et_speech
        fun appendEntry(time: Long, message: String) {
            et_entries.text.append("\n" + Entry(time, message))
        }
        fun appendLine(line:String) {
            et_entries.append(line)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FILE_PATH = applicationContext.externalCacheDir
        tv_day.text = day
        initEtEntries();
    }

    private fun initEtEntries() {
        et_entries = et_speech
        handleEditText()
        et_entries.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                writeToFile(et_entries.text.toString(),day)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    fun writeToFile(text:String,file:String) {
        val f = File(FILE_PATH,file)
        if (!f.exists()) {
            f.createNewFile()
        }
        val out = BufferedWriter(FileWriter(f))
        out.write(text)
        out.flush()
        out.close()
    }

    fun next(view:View) {
        day = day_format.format((day_format.parse(day).time + Duration.ofDays(1).toMillis()))
        tv_day.text = day
        handleEditText()
        if(isCurrentDay(day)){
            btn_next.isEnabled = false
        }
    }

    private fun handleEditText() {
        val f = File(FILE_PATH,day)
        if (f.exists()) {
            et_entries.setText(loadText(f))
        } else {
            et_entries.setText("")
        }
    }

    private fun loadText(f:File):String {
        val input = FileReader(f)
        var text = (input.readText())
        input.close()
        return text
    }

    fun  isCurrentDay(day:String):Boolean {
        return day == day_format.format(Date().time)
    }

    fun previous(view:View) {
        day = day_format.format((day_format.parse(day).time - Duration.ofDays(1).toMillis()))
        tv_day.text = day
        handleEditText()
        btn_next.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
    }

    fun speak(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        time = System.currentTimeMillis()
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Need to speak"
        )
        try {
            startActivityForResult(intent, REQ_CODE)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(
                applicationContext,
                "Sorry your device not supported",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE -> {
                if (resultCode == RESULT_OK && null != data) {
                    val result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if(result[0] !== "") {
                        appendEntry(time, result[0]);
                    }
                }
            }
        }
    }
}
