package com.watch.timetracker

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*


class ListenerService : WearableListenerService() {

    override fun onDataChanged(p0: DataEventBuffer?) {
        super.onDataChanged(p0)

        val toast_duration = Toast.LENGTH_LONG

        // Check the data type
        if (p0 != null) {
            for (event in p0) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    // Check the data path
                    val path = event.dataItem.uri.path
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    Log.v("dataListener", "DataMap received on watch: $dataMap")

                }
            }
        }
    }

}