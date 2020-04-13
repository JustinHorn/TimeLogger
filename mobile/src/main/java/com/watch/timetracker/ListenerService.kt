package com.watch.timetracker

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*


class ListenerService : WearableListenerService() {

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)
         for (event in p0) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                Log.v("recieveData","dataMap recieved $dataMap")
                addContentToText(dataMap)
                messageWatch()
            }
        }
    }

    fun addContentToText(dataMap:DataMap) {
        var count = dataMap.getInt("number")
        var main_count = MainActivity.count +1
        for (i in main_count..count) {
            val s = dataMap.getString("message"+i)
            MainActivity.appendLine(s)
        }
        MainActivity.count = count
    }

    private fun messageWatch() {
        TODO("Not yet implemented")
    }
}
