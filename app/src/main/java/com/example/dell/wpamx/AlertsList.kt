package com.example.dell.wpamx

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.dell.wpamx.databinding.FragmentAlertsListBinding
import java.util.*
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.gson.Gson

data class Alert(val uuid: UUID,
                 val pairName: String,
                 val coin: String,
                 val coinTo: String,
                 val fixedChangeThreshold: Double?,
                 val moreOrLess: Direction,
                 val exchange: String)

//interface AlertInterface {
//    fun removeAlert(alert: Alert)
//}

class AlertsList : Fragment() {

    private lateinit var binding: FragmentAlertsListBinding
    lateinit var data: ArrayList<Alert>

//    override fun removeAlert(alert: Alert) {
//        data.remove(alert)
//        (this.context!! as MainActivityHelperInterface).removeEntry(alert)
//        binding.alertsList.adapter = AlertAdapter(data, this.context!!)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alerts_list, container, false)

        binding.alertsList.layoutManager = LinearLayoutManager(this.context!!)

        if (savedInstanceState != null) {
            val gson = Gson()
            val ls = savedInstanceState.getStringArrayList("alerts")
            data = arrayListOf()
            ls?.mapTo(data) { s -> gson.fromJson(s, Alert::class.java) }
            binding.alertsList.adapter = AlertAdapter(data, this.context!!)
        } else {
            data = (this.context!! as MainActivityHelperInterface).data
            binding.alertsList.adapter = AlertAdapter(data, this.context!!)
        }


        binding.buttonAdd.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_alertsList2_to_add_alert3))

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gson = Gson()
        val list = java.util.ArrayList<String>()
        outState.putStringArrayList("alerts", data.mapTo(list) { x -> gson.toJson(x) })
    }
}