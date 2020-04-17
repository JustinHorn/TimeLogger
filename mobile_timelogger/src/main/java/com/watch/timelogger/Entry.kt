package com.watch.timelogger

import java.util.*

class Entry(private val time:Long, private val message:String) {

    override fun toString(): String {
        return convertLongToTime(time) +" "+message
    }

    fun convertLongToTime(time: Long): String {
        return MainActivity.entry_format.format(Date(time))
    }
}