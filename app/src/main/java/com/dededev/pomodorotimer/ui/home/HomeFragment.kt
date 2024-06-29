package com.dededev.pomodorotimer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dededev.pomodorotimer.databinding.FragmentHomeBinding
import android.os.CountDownTimer
import com.dededev.pomodorotimer.R
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private lateinit var timer: CountDownTimer
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isTimerRunning = false
    private var startTime: Long = 100000L
    private var timeLeft: Long = startTime
    private var pausedTimeInMillis: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnTimerPlay.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
                binding.btnTimerPlay.text = getString(R.string.play)
            } else {
                startTimer(timeLeft)
                binding.btnTimerPlay.text = getString(R.string.pause)
            }
        }

        binding.btnTimerReset.setOnClickListener {
            resetTimer()
        }

        updateTimer(binding.homeTimer)
        return root
    }

    private fun startTimer(totalTime: Long) {
        timer = object : CountDownTimer(totalTime, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateTimer(binding.homeTimer)
            }

            override fun onFinish() {
                updateTimer(binding.homeTimer)
            }
        }.start()

        isTimerRunning = true
    }

    private fun pauseTimer() {
        pausedTimeInMillis = timeLeft
        timer.cancel()
        isTimerRunning = false
    }

    private fun updateTimer(timer: TextView) {
        val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60

        timer.text =  String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun resetTimer() {
        timer.cancel()
        isTimerRunning = false
        timeLeft = startTime
        updateTimer(binding.homeTimer)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}