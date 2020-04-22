package com.watch.timelogger

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.watch.project.Constants

class SendToDataLayerThread(private val dataMap:DataMap, private val context: Context): Thread() {


    override fun run() {
        val dataClient: DataClient = Wearable.getDataClient(context)
        val pDMR:PutDataMapRequest = PutDataMapRequest.create(Constants.MOBILE_DATA_PATH)
        pDMR.dataMap.putAll(dataMap)
        val request = pDMR.asPutDataRequest()
        dataClient.putDataItem(request)
    }


}