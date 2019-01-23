package com.example.dell.wpamx

import android.util.Log
import com.google.gson.Gson
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class K(val l: Double)
data class Data(val k: K)
data class Bin(val data: Data)
class WSHandler(val threshold: Double, val direction: Direction, val removeAlert: Lazy<Unit>, val notify: Lazy<Void?>) : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val gson = Gson()
        Log.i("asd","OM2 OM2 ${gson.fromJson(text, Bin::class.java)}")
        val result = gson.fromJson(text, Bin::class.java)
        when (direction) {
            Direction.LESS ->
                if (result.data.k.l < threshold) {
                    notify.value
                    removeAlert.value
                }
            Direction.MORE ->
                if (result.data.k.l > threshold) {
                    notify.value
                    removeAlert.value
                }

        }
    }

}