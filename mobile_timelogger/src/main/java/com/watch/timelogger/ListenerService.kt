package com.watch.timelogger

import android.util.Log
import com.google.android.gms.wearable.*
import com.watch.project.Constants


class ListenerService : WearableListenerService() {

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)
         for (event in p0) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                Log.v("recieveData","dataMap recieved $dataMap")
                addContentToText(dataMap)
                putUsedCountAsDatItem(dataMap.getInt(Constants.number_key))
            }
        }
    }
    fun addContentToText(dataMap:DataMap) {
        var count = dataMap.getInt(Constants.number_key)
        var text = ""
            for (i in 0..count) {
                text += dataMap.getString(Constants.message_key+ i)
            }
        MainActivity.FILE_PATH = applicationContext.externalCacheDir
        text = MainActivity.removeRepetitions(text)
        MainActivity.appendLine(text)
    }

    private fun putUsedCountAsDatItem(number:Int) {
        var dataMap:DataMap = DataMap()
        dataMap.putInt(Constants.number_key,number)
        SendToDataLayerThread(dataMap,baseContext).start()
    }

}
