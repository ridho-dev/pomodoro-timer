package com.dededev.pomodorotimer.ui.home

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var timer: CountDownTimer
    private val _timeLeftInMillis = MutableLiveData<Long>()
    private val _isTimerRunning = MutableLiveData<Boolean>()
    val timeLeftInMillis: LiveData<Long> get() = _timeLeftInMillis
    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning
    val initialTime = 10999L
    val countDownInterval = minOf(initialTime / 500, 1000L)

    init {
        _isTimerRunning.value = false
        if (_isTimerRunning.value == true) {
            startTimer()
        }
    }

    fun startTimer() {
        timer = object : CountDownTimer(_timeLeftInMillis.value ?: initialTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }

            override fun onFinish() {
                _timeLeftInMillis.value = 0L
            }

        }.start()
        _isTimerRunning.value = true
    }

    fun pauseTimer() {
        timer.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        timer.cancel()
        _isTimerRunning.value = false
        _timeLeftInMillis.value = initialTime
    }
}