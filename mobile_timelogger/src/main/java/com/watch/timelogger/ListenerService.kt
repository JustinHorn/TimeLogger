package com.watch.timelogger

import android.util.Log
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
                putUsedCountAsDatItem(dataMap.getInt("number"))
            }
        }
    }
    fun addContentToText(dataMap:DataMap) {
        var count = dataMap.getInt("number")
        var text = ""
            for (i in 0..count) {
                text += dataMap.getString("message" + i)
            }
        MainActivity.FILE_PATH = applicationContext.externalCacheDir
        text = MainActivity.removeRepetitions(text)
        MainActivity.appendLine(text)
    }

    private fun putUsedCountAsDatItem(number:Int) {
        var dataMap:DataMap = DataMap()
        dataMap.putInt("number",number)
        SendToDataLayerThread(MainActivity.DATA_PATH_SEND,dataMap,baseContext).start()
    }

}
