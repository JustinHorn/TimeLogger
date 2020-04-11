package com.watch.timetracker

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*

class SendToDataLayerThread(private val path:String, private val dataMap:DataMap, private val context: Context): Thread() {
    //TODO("maybe turn this into a coroutine")

    override fun run() {
        val toast_duration = Toast.LENGTH_LONG


        val putDMR: PutDataMapRequest = PutDataMapRequest.create(path);
        putDMR.dataMap.putAll(dataMap);
        val request: PutDataRequest = putDMR.asPutDataRequest();
        val result: DataItem  = Tasks.await(Wearable.getDataClient(context).putDataItem( request))
        Looper.prepare()
        if (result.isDataValid) {
            Log.v("sendData", "DataMap: $dataMap sent successfully to data layer ");
        }
        else {
            Log.e("sendData", "ERROR: failed to send DataMap to data layer");
        }
    }

}