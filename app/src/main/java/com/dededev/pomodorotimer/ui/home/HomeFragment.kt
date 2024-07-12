package com.dededev.pomodorotimer.ui.home

import android.animation.ObjectAnimator
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
        binding.pbTimeProgress.max = homeViewModel.initialTime.toInt()
        binding.pbTimeProgress.progress = homeViewModel.initialTime.toInt()

        homeViewModel.timeLeftInMillis.observe(viewLifecycleOwner) {timeLeft ->
            updateTimer(binding.homeTimer, timeLeft)
            binding.pbTimeProgress.progress = timeLeft.toInt()
        }

        homeViewModel.isTimerRunning.observe(viewLifecycleOwner) {isRunning ->
            if (isRunning) {
                binding.btnTimerPlay.setImageResource(R.drawable.pause_rounded_corner)
            } else {
                binding.btnTimerPlay.setImageResource(R.drawable.play)
            }
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
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60

        timer.text =  String.format("%02d:%02d",minutes, seconds)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}