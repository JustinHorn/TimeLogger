package com.watch.timelogger

import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.*
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

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
        const val DATA_PATH_SEND = "/mobile_timetracker_data"
        const val DATA_MESSAGE = "message"
        lateinit var FILE_PATH:File
        val day_format = SimpleDateFormat("E dd.MM.yyyy")
        val entry_format = SimpleDateFormat("HH:mm")
        var day = day_format.format(Date().time)
        lateinit var et_entries:EditText //basicly et_speech
        fun appendEntry(time: Long, message: String) {
            et_entries.text.append("\n" + Entry(time, message))
        }
        fun appendLine(text:String) {
            if(this::et_entries.isInitialized) {
                et_entries.append(text)
            } else {
                appendToFile(text)
            }
        }

        private fun appendToFile(text:String) {
            if (day == day_format.format(Date().time)) {
                writeOrAppendToFile(text, day, true)
            } else {
                throw Exception("'day' not today! day: $day and today: ${day_format.format(Date().time)}")
            }
        }

        fun writeToFile(text:String,file:String) {
            writeOrAppendToFile(text,file,false)
        }

        fun writeOrAppendToFile(text:String,file:String,append:Boolean) {
            val f = File(FILE_PATH,file)
            if (!f.exists()) {
                f.createNewFile()
            }
            val out = BufferedWriter(FileWriter(f,append))
            out.write(text)
            out.flush()
            out.close()
        }

        fun removeRepetitions(text: String):String {
            var lines  = text.split("\n")
            lines = lines.filterIndexed {index:Int ,line:String -> !lines.subList(0,index).contains(line) }
            var end_text =""
            lines.forEach {line:String -> end_text += line +"\n"}
             return end_text
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
        et_entries.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var text = removeRepetitions(et_entries.text.toString())
                if (text == et_entries.text.toString()) {
                    writeToFile(et_entries.text.toString(), day)
                } else {
                    et_entries.text = SpannableStringBuilder(text)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        handleEditText()
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
