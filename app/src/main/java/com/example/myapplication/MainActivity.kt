// MainActivity.kt
package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {
    private val data: MutableList<String> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private val DATA_KEY = "data_key"
    private var countDownTimer: CountDownTimer? = null
    private var isReviewMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        val bloodPressureEditText: EditText = findViewById(R.id.bloodPressureEditText)
        val bloodOxygenEditText: EditText = findViewById(R.id.bloodOxygenEditText)
        val bloodSugarEditText: EditText = findViewById(R.id.bloodSugarEditText)
        val weightEditText: EditText = findViewById(R.id.weightEditText)
        val saveButton: Button = findViewById(R.id.saveButton)
        val dataContainer: LinearLayout = findViewById(R.id.dataContainer)
        val clearButton: Button = findViewById(R.id.clearButton)
        val reviewButton: Button = findViewById(R.id.reviewButton)

        val savedData = sharedPreferences.getStringSet(DATA_KEY, HashSet<String>())
        if (savedData != null) {
            data.addAll(savedData)
        }

        saveButton.setOnClickListener {
            val bloodPressure = bloodPressureEditText.text.toString()
            val bloodOxygen = bloodOxygenEditText.text.toString()
            val bloodSugar = bloodSugarEditText.text.toString()
            val weight = weightEditText.text.toString()
            val currentTime = System.currentTimeMillis().toString()

            val dataEntry = "Time: $currentTime, Blood Pressure: $bloodPressure, " +
                    "Blood Oxygen: $bloodOxygen, Blood Sugar: $bloodSugar, Weight: $weight"

            data.add(dataEntry)
            clearInputFields()

            val editor = sharedPreferences.edit()
            editor.putStringSet(DATA_KEY, HashSet(data))
            editor.apply()

            showRecentData(dataContainer)
        }

        clearButton.setOnLongClickListener {
            countDownTimer = object : CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // 不执行任何操作
                }

                override fun onFinish() {
                    clearData()
                    dataContainer.removeAllViews()
                }
            }
            countDownTimer?.start()

            true
        }

        clearButton.setOnClickListener {
            countDownTimer?.cancel()
        }

        reviewButton.setOnClickListener {
            if (isReviewMode) {
                showRecentData(dataContainer)
                isReviewMode = false
            } else {
                showRecentDataInLarge(dataContainer)
                isReviewMode = true
            }
        }


        showRecentData(dataContainer)
    }

    private fun clearInputFields() {
        val bloodPressureEditText: EditText = findViewById(R.id.bloodPressureEditText)
        val bloodOxygenEditText: EditText = findViewById(R.id.bloodOxygenEditText)
        val bloodSugarEditText: EditText = findViewById(R.id.bloodSugarEditText)
        val weightEditText: EditText = findViewById(R.id.weightEditText)

        bloodPressureEditText.text.clear()
        bloodOxygenEditText.text.clear()
        bloodSugarEditText.text.clear()
        weightEditText.text.clear()
    }

    private fun showRecentData(container: LinearLayout) {
        container.removeAllViews()

        val recentData = data.sortedByDescending { getDataTime(it) }.take(7)
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        val tableLayout = TableLayout(this)
        tableLayout.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
        tableLayout.isStretchAllColumns = true
        container.addView(tableLayout)

        // 创建表头
        val headerRow = TableRow(this)
        headerRow.setBackgroundColor(Color.LTGRAY)
        headerRow.addView(createTextView("時間"))
        headerRow.addView(createTextView("血壓"))
        headerRow.addView(createTextView("血氧"))
        headerRow.addView(createTextView("血糖"))
        headerRow.addView(createTextView("體重"))
        tableLayout.addView(headerRow)

        for (entry in recentData) {
            val parts = entry.split(", ")
            val time = parts[0].substringAfter(":").trim()
            val bloodPressure = parts[1].substringAfter(":").trim()
            val bloodOxygen = parts[2].substringAfter(":").trim()
            val bloodSugar = parts[3].substringAfter(":").trim()
            val weight = parts[4].substringAfter(":").trim()

            val tableRow = TableRow(this)
            tableRow.addView(createTextView(dateFormat.format(Date(time.toLong()))))
            tableRow.addView(createTextView(bloodPressure))
            tableRow.addView(createTextView(bloodOxygen))
            tableRow.addView(createTextView(bloodSugar))
            tableRow.addView(createTextView(weight))
            tableLayout.addView(tableRow)
        }
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(16, 16, 16, 16)
        return textView
    }

    private fun generateTable(container: LinearLayout) {
        container.removeAllViews()

        val recentData = data.sortedByDescending { getDataTime(it) }.take(7)
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        val tableLayout = TableLayout(this)
        tableLayout.layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT)
        tableLayout.isStretchAllColumns = true
        container.addView(tableLayout)

        // 创建表头
        val headerRow = TableRow(this)
        headerRow.setBackgroundColor(Color.LTGRAY)
        headerRow.addView(createTextView("時間"))
        headerRow.addView(createTextView("血壓"))
        headerRow.addView(createTextView("血氧"))
        headerRow.addView(createTextView("血糖"))
        headerRow.addView(createTextView("體重"))
        tableLayout.addView(headerRow)

        for (entry in recentData) {
            val parts = entry.split(", ")
            val time = parts[0].substringAfter(":").trim()
            val bloodPressure = parts[1].substringAfter(":").trim()
            val bloodOxygen = parts[2].substringAfter(":").trim()
            val bloodSugar = parts[3].substringAfter(":").trim()
            val weight = parts[4].substringAfter(":").trim()

            val tableRow = TableRow(this)
            tableRow.addView(createTextView(dateFormat.format(Date(time.toLong()))))
            tableRow.addView(createTextView(bloodPressure))
            tableRow.addView(createTextView(bloodOxygen))
            tableRow.addView(createTextView(bloodSugar))
            tableRow.addView(createTextView(weight))
            tableLayout.addView(tableRow)
        }
    }

    private fun getDataTime(entry: String): Long {
        val parts = entry.split(", ")
        val time = parts[0].substringAfter(":").trim()
        return time.toLong()
    }

    private fun clearData() {
        data.clear()
        val editor = sharedPreferences.edit()
        editor.remove(DATA_KEY)
        editor.apply()
    }


    private fun showRecentDataInLarge(container: LinearLayout) {
        container.removeAllViews()

        val recentData = data.sortedByDescending { getDataTime(it) }.take(7)
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        for (entry in recentData) {
            val parts = entry.split(", ")
            val time = parts[0].substringAfter(":").trim()
            val bloodPressure = parts[1].substringAfter(":").trim()
            val bloodOxygen = parts[2].substringAfter(":").trim()
            val bloodSugar = parts[3].substringAfter(":").trim()
            val weight = parts[4].substringAfter(":").trim()

            val textView = EditText(this)
            textView.setText("時間: ${dateFormat.format(Date(time.toLong()))}\n" +
                    "血壓: $bloodPressure\n" +
                    "血氧: $bloodOxygen\n" +
                    "血糖: $bloodSugar\n" +
                    "體重: $weight")
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            container.addView(textView)
        }
    }

}
