package com.example.currencyexchange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sourceAmount: EditText
    private lateinit var targetAmount: EditText
    private lateinit var spinnerSourceCurrency: Spinner
    private lateinit var spinnerTargetCurrency: Spinner
    private lateinit var exchangeRateText: TextView
    private lateinit var lastUpdatedText: TextView

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "GBP" to 0.76,
        "JPY" to 110.0,
        "AUD" to 1.35
    )

    private var selectedSourceCurrency = "USD"
    private var selectedTargetCurrency = "EUR"
    private var isSourceAmountActive = true  // Flag to keep track of which EditText is the source

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceAmount = findViewById(R.id.sourceAmount)
        targetAmount = findViewById(R.id.targetAmount)
        spinnerSourceCurrency = findViewById(R.id.spinnerSourceCurrency)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)
        exchangeRateText = findViewById(R.id.exchangeRateText)

        setupSpinners()
        setupButtons()
        setupAmountListeners()
        updateExchangeRateText()
    }

    private fun setupSpinners() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "AUD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSourceCurrency.adapter = adapter
        spinnerTargetCurrency.adapter = adapter

        spinnerSourceCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedSourceCurrency = currencies[position]
                calculateConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTargetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedTargetCurrency = currencies[position]
                calculateConversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupAmountListeners() {
        sourceAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isSourceAmountActive) calculateConversion()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        targetAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isSourceAmountActive) calculateConversion()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        sourceAmount.setOnClickListener {
            isSourceAmountActive = true
            // Set text to bold
            sourceAmount.setTypeface(null, android.graphics.Typeface.BOLD)
            // Revert the other field to normal
            targetAmount.setTypeface(null, android.graphics.Typeface.NORMAL)
            calculateConversion()
        }

        targetAmount.setOnClickListener {
            isSourceAmountActive = false
            // Set text to bold
            targetAmount.setTypeface(null, android.graphics.Typeface.BOLD)
            // Revert the other field to normal
            sourceAmount.setTypeface(null, android.graphics.Typeface.NORMAL)
            calculateConversion()
        }
    }


    private fun calculateConversion() {
        val sourceText = if (isSourceAmountActive) sourceAmount.text.toString() else targetAmount.text.toString()
        val sourceValue = sourceText.toDoubleOrNull() ?: 0.0
        val sourceRate = if (isSourceAmountActive) exchangeRates[selectedSourceCurrency] ?: 1.0 else exchangeRates[selectedTargetCurrency] ?: 1.0
        val targetRate = if (isSourceAmountActive) exchangeRates[selectedTargetCurrency] ?: 1.0 else exchangeRates[selectedSourceCurrency] ?: 1.0

        val targetValue = sourceValue * (targetRate / sourceRate)

        if (isSourceAmountActive) {
            targetAmount.setText(String.format("%.2f", targetValue))
        } else {
            sourceAmount.setText(String.format("%.2f", targetValue))
        }

        updateExchangeRateText()
    }

    private fun updateExchangeRateText() {
        val sourceRate = exchangeRates[selectedSourceCurrency] ?: 1.0
        val targetRate = exchangeRates[selectedTargetCurrency] ?: 1.0
        val exchangeRate = targetRate / sourceRate
        exchangeRateText.text = "1 $selectedSourceCurrency = %.4f $selectedTargetCurrency".format(exchangeRate)
    }

    private fun setupButtons() {
        val buttonIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.buttonDot
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener { button ->
                onNumberButtonClick((button as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.buttonC).setOnClickListener { onClearButtonClick() }
        findViewById<Button>(R.id.buttonBS).setOnClickListener { onBackspaceButtonClick() }
    }

    private fun onNumberButtonClick(value: String) {
        val currentText = if (isSourceAmountActive) sourceAmount.text.toString() else targetAmount.text.toString()
        val newText = currentText + value
        if (isSourceAmountActive) {
            sourceAmount.setText(newText)
        } else {
            targetAmount.setText(newText)
        }
    }

    private fun onClearButtonClick() {
        sourceAmount.setText("")
        targetAmount.setText("")
    }

    private fun onBackspaceButtonClick() {
        val currentText = if (isSourceAmountActive) sourceAmount.text.toString() else targetAmount.text.toString()
        if (currentText.isNotEmpty()) {
            val newText = currentText.substring(0, currentText.length - 1)
            if (isSourceAmountActive) {
                sourceAmount.setText(newText)
            } else {
                targetAmount.setText(newText)
            }
        }
    }
}

