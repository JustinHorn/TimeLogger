package com.watch.timetracker

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*
import java.util.*


class ListenerService : WearableListenerService() {

    override fun onDataChanged(p0: DataEventBuffer?) {
        super.onDataChanged(p0)

        val toast_duration = Toast.LENGTH_LONG

        var dataMap: DataMap
        // Check the data type
        if (p0 != null) {
            for (event in p0) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    // Check the data path
                    val path = event.dataItem.uri.path
                    if (path == MainActivity.DATA_PATH_RECEIVED) {
                    }
                    dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    Log.v("dataListener", "DataMap received on watch: $dataMap")

                    val toast = Toast.makeText(applicationContext, getString(R.string.message_received_success), toast_duration);
                    toast.show()
                    val dataMap = DataMap()
                    dataMap.putLong(MainActivity.DATA_TIME, Date().time)
                    dataMap.putString(MainActivity.DATA_MESSAGE, "")
                    //Requires a new thread to avoid blocking the UI
                    SendToDataLayerThread(MainActivity.DATA_PATH_SEND, dataMap,baseContext).start();
                }
            }
        }
    }

}