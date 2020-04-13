package com.watch.timetracker

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*

class SendToDataLayerThread(private val path:String, private val dataMap:DataMap, private val context: Context): Thread() {


    override fun run() {
        val putDMR: PutDataMapRequest = PutDataMapRequest.create(path);
        putDMR.dataMap.putAll(dataMap);
        val request: PutDataRequest = putDMR.asPutDataRequest();
        val result: DataItem  = Tasks.await(Wearable.getDataClient(context).putDataItem( request))
        if (result.isDataValid) {
            Log.v("myTag", "DataMap: $dataMap sent successfully to data layer ");
        }
        else {
            Log.v("myTag", "ERROR: failed to send DataMap to data layer");
        }
    }

}