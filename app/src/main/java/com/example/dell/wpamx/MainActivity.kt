package com.example.dell.wpamx

import android.app.Notification
import android.app.Notification.DEFAULT_VIBRATE
import android.app.NotificationManager
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.dell.wpamx.databinding.ActivityMainBinding
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import spark.Spark.*

class MainActivity : AppCompatActivity(), MainActivityHelperInterface {

    private lateinit var binding: ActivityMainBinding
    override var data: ArrayList<Alert> = arrayListOf()
    var tasks: ArrayList<Pair<UUID, AsyncTask<Alert, Void, Void>>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        saveState(savedInstanceState)
    }

    private fun saveState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val gson = Gson()
            val ls = savedInstanceState.getStringArrayList("alerts")
            ls?.mapTo(data) { s -> gson.fromJson(s, Alert::class.java) }
        }
    }

    override fun addEntry(data: Alert) {
        this.data.add(data)
        this.createNotification(data)
    }

    override fun removeEntry(alert: Alert) {
        this.data.remove(alert)
        this.removeNotification(alert)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gson = Gson()
        val list = java.util.ArrayList<String>()
        outState.putStringArrayList("alerts", data.mapTo(list) { x -> gson.toJson(x) })
    }

    val self = this


    inner class GetMethodEx1 : AsyncTask<Alert, Void, Void>() {

        private lateinit var mContext: Context
        private val NOTIFICATION_ID: Int = Random.nextInt(1, Int.MAX_VALUE)
        private lateinit var mNotificationManager: NotificationManager

        override fun doInBackground(vararg params: Alert?): Void? {
            mContext = self
            val alert: Alert = params[0]!!

            val title = "Threshold hit on ${alert.exchange}!"
            val body = "${alert.coin} ${alert.moreOrLess} ${alert.fixedChangeThreshold} ${alert.coinTo}."

            val client = OkHttpClient()
            val url = "wss://stream.binance.com:9443/stream?streams=${alert.coin.toLowerCase()}${alert.coinTo.toLowerCase()}@kline_1m"
            val request = Request.Builder().url(url).build()
            val threhold: Double = if(alert.fixedChangeThreshold != null) alert.fixedChangeThreshold else 0.0
            val listener = WSHandler(threhold, alert.moreOrLess, lazy { self.removeEntry(alert) }, lazy {createNotification( title, body, self)} )
            val ws = client.newWebSocket(request, listener)

            return null
        }

        private fun createNotification(contentTitle: String, contentText: String, context: Context): Void? {
            mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder: Notification.Builder  = Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setDefaults(DEFAULT_VIBRATE)
            mNotificationManager.notify(NOTIFICATION_ID, builder.build())
            return null
        }
    }

    private fun createNotification(data: Alert) {
        this.tasks.add(Pair(data.uuid, GetMethodEx1().execute(data)))
    }

    private fun removeNotification(alert: Alert) {
        val toRemove = this.tasks.find { (u, _) -> u == alert.uuid }
        toRemove?.second?.cancel(true)
        this.tasks.remove(toRemove)
    }
}

interface MainActivityHelperInterface {
    var data: ArrayList<Alert>

    fun addEntry(data: Alert)
    fun removeEntry(alert: Alert)
}