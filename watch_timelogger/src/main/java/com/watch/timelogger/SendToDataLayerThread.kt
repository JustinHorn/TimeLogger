package com.watch.timelogger

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable.getNodeClient
import com.watch.project.Constants


class SendToDataLayerThread(private val message:String, private val context: Context): Thread() {

    override fun run() {
        val dataClient = Wearable.getDataClient(context)
        val dataMap = makeMap(dataClient,message)
        putMapOnDataLayer(dataClient,dataMap)
    }

    companion object {
        fun getUri(context:Context):Uri {
            val node = Tasks.await(getNodeClient(context).localNode)
            val uri = Uri.Builder().scheme("wear").path(Constants.WATCH_DATA_PATH)
                .authority(node.id.toString()).build()
            Log.i("sendData", "Uri build: " + uri.toString());
            return uri
        }

        fun putMapOnDataLayer(dataClient:DataClient,dataMap:DataMap){
            val putDMR: PutDataMapRequest =
                PutDataMapRequest.create(Constants.WATCH_DATA_PATH);
            Log.v("sendData", "map send: $dataMap")
            putDMR.dataMap.putAll(dataMap);
            val request: PutDataRequest = putDMR.asPutDataRequest();
            dataClient.putDataItem(request)
        }
    }

    private fun makeMap(dataClient: DataClient,message: String):DataMap {
        val uri = getUri(context)
        val dataItem = Tasks.await(dataClient.getDataItem(uri))
        val dataMap:DataMap = when(dataItem != null) {
            true -> useOldMap(dataItem, message)
            false -> makeNewMap(message)
        }
        return dataMap
    }

    private fun useOldMap(dataItem:DataItem,message:String):DataMap {
        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
        Log.i("sendData", "use old map $dataMap")
        var number1 = dataMap.getInt(Constants.number_key)
        number1++
        dataMap.putInt(Constants.number_key, number1)
        dataMap.putString(Constants.message_key + number1, message)
        return dataMap
    }

    private fun makeNewMap(message:String):DataMap {
        Log.i("sendData", "make new map")
        val dataMap = DataMap()
        dataMap.putInt(Constants.number_key, 0)
        dataMap.putString(Constants.message_key+"0", message)
        return dataMap
    }





}