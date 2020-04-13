package com.watch.timetracker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable.WearableOptions
import com.google.android.gms.wearable.Wearable.getNodeClient
import java.util.*


class SendToDataLayerThread(private val message:String, private val context: Context): Thread() {

    override fun run() {
        val dataClient = Wearable.getDataClient(context)
        val dataMap = makeMap(dataClient,message)
        putMapOnDataLayer(dataClient,dataMap)
    }
    private fun getUri():Uri {
        val node = Tasks.await(getNodeClient(context).localNode)
        val uri = Uri.Builder().scheme("wear").path(MainActivity.DATA_PATH_SEND)
            .authority(node.id.toString()).build()
        Log.i("sendData", "Uri build: " + uri.toString());
        return uri
    }

    private fun makeMap(dataClient: DataClient,message: String):DataMap {
        val uri = getUri()
        val dataItem = Tasks.await(dataClient.getDataItem(uri))
        val dataMap:DataMap = when(dataItem.isDataValid) {
            true -> useOldMap(dataItem, message)
            false -> makeNewMap(message)
        }
        return dataMap
    }

    private fun useOldMap(dataItem:DataItem,message:String):DataMap {
        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
        Log.i("sendData", "use old map $dataMap")
        var number = dataMap.getInt("number")
        number++
        dataMap.putInt("number", number)
        dataMap.putString("message" + number, message)
        return dataMap
    }

    private fun makeNewMap(message:String):DataMap {
        Log.i("sendData", "make new map")
        val dataMap = DataMap()
        dataMap.putInt("number", 0)
        dataMap.putString("number0", message)
        return dataMap
    }

    private fun putMapOnDataLayer(dataClient:DataClient,dataMap:DataMap){
        val putDMR: PutDataMapRequest =
            PutDataMapRequest.create(MainActivity.DATA_PATH_SEND);
        Log.v("sendData", "map send: $dataMap")
        putDMR.dataMap.putAll(dataMap);
        val request: PutDataRequest = putDMR.asPutDataRequest();
        dataClient.putDataItem(request)
    }



}