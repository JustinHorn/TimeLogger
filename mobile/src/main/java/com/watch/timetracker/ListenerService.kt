package com.watch.timetracker

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*


class ListenerService : WearableListenerService() {

    override fun onDataChanged(p0: DataEventBuffer?) {
        super.onDataChanged(p0)

        var dataMap: DataMap
        // Check the data type
        if (p0 != null) {
            for (event in p0) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    val path = event.dataItem.uri.path
                    if (path == MainActivity.DATA_PATH_RECEIVED) {
                    }
                    dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    Log.v("dataListener", "DataMap received on watch: $dataMap")
                    val toast = Toast.makeText(applicationContext, "Message received successfully", Toast.LENGTH_LONG);
                    toast.show()

                    MainActivity.appendEntry(dataMap.getLong("time"),dataMap.getString("message"));
                  }
            }
        }
    }


}