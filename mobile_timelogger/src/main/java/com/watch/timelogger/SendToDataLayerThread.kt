package com.watch.timelogger

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*

class SendToDataLayerThread(private val path:String, private val dataMap:DataMap, private val context: Context): Thread() {


    override fun run() {
        val dataClient: DataClient = Wearable.getDataClient(context)
        val pDMR:PutDataMapRequest = PutDataMapRequest.create(path)
        pDMR.dataMap.putAll(dataMap)
        val request = pDMR.asPutDataRequest()
        dataClient.putDataItem(request)
    }

    private fun test() {
        var capabilityClient = Wearable.getCapabilityClient(context)
        var answer = Tasks.await(capabilityClient.getAllCapabilities(CapabilityClient.FILTER_ALL))
        Log.v("test","answer: $answer")

        val nodeClient = Wearable.getNodeClient(context)
        var nodes = Tasks.await(nodeClient.connectedNodes)
        val messageClient:MessageClient = Wearable.getMessageClient(context)
        nodes.forEach { node: Node -> messageClient.sendMessage(node.id,"/whatsup","I hate this system".toByteArray())}
    }

}