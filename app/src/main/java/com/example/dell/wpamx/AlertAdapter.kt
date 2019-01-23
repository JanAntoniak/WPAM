package com.example.dell.wpamx

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alerts_list_item.view.*

class AlertAdapter(private val items : ArrayList<Alert>, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.alerts_list_item, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val it = items[p1]
        val sign = if (it.moreOrLess == Direction.LESS) "<" else ">"
        val text = "${it.coin} / ${it.coinTo} \n${it.coin} $sign ${it.fixedChangeThreshold} ${it.coinTo} \n${it.exchange}"
        p0.alertType.textView5.text = text
        p0.alertType.button2.setOnClickListener {
            if (items.size >= p1+1) {
                val toRemove = items[p1]
                items.removeAt(p1)
                notifyItemRemoved(p1)
                notifyItemRangeChanged(p1, items.size)
                (context as MainActivityHelperInterface).removeEntry(toRemove)

            }
            // Here should be done sth like: find thread and kill it
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val alertType = view.alert_type!!
}