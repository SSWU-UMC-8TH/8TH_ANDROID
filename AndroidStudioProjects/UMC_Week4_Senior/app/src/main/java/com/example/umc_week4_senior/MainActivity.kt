package com.example.umc_week4_senior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnClear: Button


    private var isRunning = false
    private var timeMillis = 0L
    private var startTime = 0L


    private var timerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvTimer = findViewById(R.id.tvTimer)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnClear = findViewById(R.id.btnClear)


        btnStart.setOnClickListener {
            startStopwatch()
        }

        btnPause.setOnClickListener {
            pauseStopwatch()
        }

        btnClear.setOnClickListener {
            clearStopwatch()
        }
    }

    private fun startStopwatch() {
        if (!isRunning) {
            isRunning = true
            startTime = System.currentTimeMillis()

            btnStart.visibility = View.GONE
            btnPause.visibility = View.VISIBLE

            timerJob = lifecycleScope.launch {
                while (isRunning) {
                    delay(10)
                    val currentTime = System.currentTimeMillis()
                    val elapsed = timeMillis + (currentTime - startTime)

                    updateTimerText(elapsed)
                }
            }
        }
    }


    private fun pauseStopwatch() {
        if (isRunning) {
            isRunning = false
            timerJob?.cancel()
            timerJob = null
            timeMillis += (System.currentTimeMillis() - startTime)

            btnPause.visibility = View.GONE
            btnStart.visibility = View.VISIBLE
        }
    }


    private fun clearStopwatch() {
        if (isRunning) {
            isRunning = false
            timerJob?.cancel()
            timerJob = null
            timeMillis = 0L
            updateTimerText(0L)

            btnPause.visibility = View.GONE
            btnStart.visibility = View.VISIBLE
        }
        else {
            timeMillis = 0L
            updateTimerText(0L)
        }
    }

    private fun updateTimerText(ms: Long) {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val displaySeconds = seconds % 60
        val displayMillis = (ms % 1000) / 10

        val formatted = String.format(
            "%02d:%02d:%02d",
            minutes,
            displaySeconds,
            displayMillis
        )

        tvTimer.text = formatted
    }
}