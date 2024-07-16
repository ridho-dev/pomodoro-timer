package com.dededev.pomodorotimer.ui.home

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var timer: CountDownTimer
    private val _timeLeftInMillis = MutableLiveData<Long>()
    private val _isTimerRunning = MutableLiveData<Boolean>()
    private val _currentTimerType = MutableLiveData<TimerType>()
    private val _initialTime = MutableLiveData<Long>()
    private val _sessionCount = MutableLiveData<Int>()
    val timeLeftInMillis: LiveData<Long> get() = _timeLeftInMillis
    val currentTimerType: LiveData<TimerType> get() = _currentTimerType
    val isTimerRunning: LiveData<Boolean> get() = _isTimerRunning
    val initialTime: LiveData<Long> get() = _initialTime
    val sessionCount: LiveData<Int> get() =_sessionCount
    val sessionTotal = 3
    private val initialFocusTime = 4000L - 1L
    private val initialBreakTime = 2000L - 1L

    init {
        setToInitialState()
    }

    fun startTimer() {
        val currentInitialTime =  _initialTime.value ?: initialFocusTime
        val countDownInterval = minOf(maxOf(currentInitialTime / 2000, 10L), 1000L)
        timer = object : CountDownTimer(_timeLeftInMillis.value ?: currentInitialTime, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeftInMillis.value = millisUntilFinished
            }
            override fun onFinish() {
                _timeLeftInMillis.value = 0L
                if (isLastSession() && isTimerTypeBreak()) {
                    resetTimer()
                    setToInitialState()
                } else {
                    addSession()
                    switchTimer()
                    startTimer()
                }
            }
        }.start()
        _isTimerRunning.value = true
    }

    private fun addSession() {
        if (isTimerTypeBreak()) {
            _sessionCount.value = _sessionCount.value?.plus(1)
        }
    }

    private fun isTimerTypeBreak(): Boolean {
        return _currentTimerType.value == TimerType.BREAK
    }

    private fun isLastSession(): Boolean {
        return (_sessionCount.value ?: 1) == sessionTotal
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

    private fun setToInitialState() {
        _isTimerRunning.value = false
        _currentTimerType.value = TimerType.FOCUS
        _timeLeftInMillis.value = initialFocusTime
        _initialTime.value = initialFocusTime
        _sessionCount.value = 1
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