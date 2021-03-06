package com.watch.timelogger

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*
import com.watch.project.Constants


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
                    var dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val number = dataMap.getInt("number")
                    if(number >= 0 ) {
                        val dataClient: DataClient = Wearable.getDataClient(baseContext)
                        dataMap = DataMap()
                        dataMap.putInt("number",-1)
                        SendToDataLayerThread.putMapOnDataLayer(dataClient,dataMap)
                    }

                    Log.v("dataListener", "DataMap received on watch: $dataMap")

                }
            }
        }
    }

}