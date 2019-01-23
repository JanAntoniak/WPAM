package com.example.dell.wpamx

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.navigation.Navigation
import com.example.dell.wpamx.databinding.FragmentAddAlertBinding
import java.util.*
import android.text.Editable
import android.text.TextWatcher



enum class Direction {
    MORE, LESS;
    override fun toString(): String {
        return when (this) {
            Direction.LESS -> "<"
            Direction.MORE -> ">"
        }
    }
}

class add_alert : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentAddAlertBinding
    private val fiatMap: Map<String, Map<Int, String>> = mapOf(
        "Binance" to mapOf(0 to "USDT"),
        "Bitbay" to mapOf(0 to "USD", 1 to "EUR", 2 to "PLN")
    )
    private val coinMap: Map<Int, String> = mapOf(0 to "BTC", 1 to "ETH", 2 to "XRP", 3 to "LSK")

    private var direction: Direction = Direction.MORE
    private var coin: String = "BTC"
    private var fiat: String = "USDT"
    private var threshold: Double = 0.00
    private var exchange: String = "Binance"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_alert, container, false)

        initializeButtons()
        initializeExchangeSpinners()
        setText()
        return binding.root
    }

    private fun initializeButtons() {
        binding.button.setOnClickListener { view: View ->
//            (this.context!! as MainActivityHelperInterface).data.add(takeCurrentAlert())
            (this.context!! as MainActivityHelperInterface).addEntry(takeCurrentAlert())
            Navigation.findNavController(view).popBackStack()
        }

        binding.button4.setOnClickListener { view: View ->
            Navigation.findNavController(view).popBackStack()
        }

        binding.radioLess.setOnClickListener{v -> onRadioButtonClicked(v)}
        binding.radioMore.setOnClickListener{v -> onRadioButtonClicked(v)}
        binding.radioGroup.check(binding.radioMore.id)

        binding.editText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    threshold = s.toString().toDouble()
                    setText()
                } else {
                    threshold = 0.0
                    setText()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        val self = this
        binding.spinnerFiat.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fiat = fiatMap.get(self.exchange)!![position]!!
                setText()
            }
        })
        binding.spinnerCoin.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                coin = coinMap[position]!!
                setText()
            }})
    }

    private fun setText() {
        val option = if (this.direction == Direction.LESS) "less" else "more"
        binding.textView2.text = "Alert me when $coin is $option than $threshold $fiat"
    }

    private fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_less -> if (checked) {
                    direction = Direction.LESS
                }
                R.id.radio_more -> if (checked) {
                    direction = Direction.MORE
                }
            }
            setText()
        }
    }

    private fun takeCurrentAlert(): Alert {
        return Alert(UUID.randomUUID(), "${coin}/${fiat}", coin, fiat, threshold, direction, exchange)
    }

    private fun initializeExchangeSpinners() {
        createExchangeSpinnerDropdown()
        createCoinSpinnerDropdown()
        binding.spinnerExchange.onItemSelectedListener = this
    }

    private fun createExchangeSpinnerDropdown() {
        val spinnerExchange: Spinner = binding.spinnerExchange
        ArrayAdapter.createFromResource(this.context!!, R.array.exchanges, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerExchange.adapter = adapter
            }
    }

    private fun createCoinSpinnerDropdown() {
        val spinnerArray = coinMap.values.toList()
        val spinnerArrayAdapter = ArrayAdapter<String>(this.context!!, android.R.layout.simple_spinner_item, spinnerArray)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCoin.adapter = spinnerArrayAdapter
    }

    private val binanceFiat = arrayOf("USDT")
    private val bitbayFiat = arrayOf("USD", "EUR", "PLN")
    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val (exchange, spinnerArray) = when (position) {
            0 -> Pair("Binance", binanceFiat)
            1 -> Pair("Bitbay", bitbayFiat)
            else -> Pair("Binance", binanceFiat)
        }
        this.exchange = exchange
        setText()

        val spinnerArrayAdapter = ArrayAdapter<String>(this.context!!, android.R.layout.simple_spinner_item, spinnerArray)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFiat.adapter = spinnerArrayAdapter
    }
}
