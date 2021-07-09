package com.example.foregroundservice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.foregroundservice.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTime = System.currentTimeMillis()

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                binding.timerView.text = (System.currentTimeMillis() - startTime).displayTime()
                delay(INTERVAL)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    private companion object {

        private const val INTERVAL = 10L
    }
}