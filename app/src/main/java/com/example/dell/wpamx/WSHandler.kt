package com.example.dell.wpamx

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class WSHandler : WebSocketListener() {

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.i("OM OM OM ${text}", "OM OM OM ${text}")
    }

}