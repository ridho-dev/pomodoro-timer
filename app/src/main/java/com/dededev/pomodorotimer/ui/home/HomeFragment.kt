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
import android.util.Log
import com.dededev.pomodorotimer.R
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.timeLeftInMillis.observe(viewLifecycleOwner) {timeLeft ->
            updateTimer(binding.homeTimer, timeLeft)
        }

        homeViewModel.isTimerRunning.observe(viewLifecycleOwner) {isRunning ->
            binding.btnTimerPlay.text = if (isRunning) getString(R.string.pause) else getString(R.string.play)
        }

        updateTimer(binding.homeTimer, homeViewModel.initialTime)

        binding.btnTimerPlay.setOnClickListener {
            if (homeViewModel.isTimerRunning.value == true) {
                homeViewModel.pauseTimer()
            } else {
                homeViewModel.startTimer()
            }
        }

        binding.btnTimerReset.setOnClickListener { homeViewModel.resetTimer() }


        return root
    }

    private fun updateTimer(timer: TextView, timeLeft: Long) {
        val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60

        timer.text =  String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}