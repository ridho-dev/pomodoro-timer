package com.dededev.pomodorotimer.ui.home

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var timer: CountDownTimer
    private val _timeLeftInMillis = MutableLiveData<Long>()
    private val _isTimerRunning = MutableLiveData<Boolean>()
    private val _currentTimerType = MutableLiveData<TimerType>()
    private val _initialTime = MutableLiveData<Long>()
    val timeLeftInMillis: LiveData<Long> get() = _timeLeftInMillis
    val currentTimerType: LiveData<TimerType> get() = _currentTimerType
    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning
    val initialTime: LiveData<Long> get() = _initialTime
    private val initialFocusTime = 3000L - 1L
    private val initialBreakTime = 1000L - 1L

    init {
        _isTimerRunning.value = false
        _currentTimerType.value = TimerType.FOCUS
        _timeLeftInMillis.value = initialFocusTime
        _initialTime.value = initialFocusTime
    }

    fun startTimer() {
        val currentInitialTime =  _initialTime.value ?: initialFocusTime
        val countDownInterval = minOf(maxOf(currentInitialTime / 2000, 10L), 1000L)
        Log.i("TAG", "$countDownInterval")
        timer = object : CountDownTimer(_timeLeftInMillis.value ?: currentInitialTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished

            }
            override fun onFinish() {
                _timeLeftInMillis.value = 0L
                switchTimer()
                startTimer()
            }
        }.start()
        _isTimerRunning.value = true
    }

    fun switchTimer() {
        _currentTimerType.value = when (_currentTimerType.value) {
            TimerType.FOCUS -> TimerType.BREAK
            TimerType.BREAK -> TimerType.FOCUS
            else -> TimerType.FOCUS
        }
        setInitialTimeForCurrentTimer()
    }

    private fun setInitialTimeForCurrentTimer() {
        _initialTime.value = when (_currentTimerType.value) {
            TimerType.FOCUS -> initialFocusTime
            TimerType.BREAK -> initialBreakTime
            else -> initialBreakTime
        }
        _timeLeftInMillis.value = _initialTime.value

    }

    fun pauseTimer() {
        timer.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        timer.cancel()
        _isTimerRunning.value = false
        setInitialTimeForCurrentTimer()
    }

    enum class TimerType {
        FOCUS, BREAK
    }
}