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
import com.dededev.pomodorotimer.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var progressAnimator: ObjectAnimator
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        progressBar = binding.pbTimeProgress

        homeViewModel.initialTime.observe(viewLifecycleOwner) {initialTime ->
            progressBar.apply {
                max = initialTime.toInt()
                progress = initialTime.toInt()
            }
            progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", initialTime.toInt() , 0)
                .setDuration(initialTime)
            if (homeViewModel.isTimerRunning.value == true) {
                progressAnimator.start()
            }
        }

        homeViewModel.currentTimerType.observe(viewLifecycleOwner) {
            binding.textView2.text = it.toString()
        }

        homeViewModel.timeLeftInMillis.observe(viewLifecycleOwner) {timeLeft ->
            updateTimer(binding.homeTimer, timeLeft)
        }

        homeViewModel.isTimerRunning.observe(viewLifecycleOwner) {isRunning ->
            if (isRunning) {
                binding.btnTimerPlay.setImageResource(R.drawable.pause_rounded_corner)
                if (progressAnimator.isPaused) {
                    progressAnimator.resume()
                } else {
                    progressAnimator.start()
                }
            } else {
                binding.btnTimerPlay.setImageResource(R.drawable.play)
                progressAnimator.pause()
            }
        }

        updateTimer(binding.homeTimer, homeViewModel.initialTime.value!!)

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

        timer.text =  String.format("%02d:%02d",minutes, seconds + 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}