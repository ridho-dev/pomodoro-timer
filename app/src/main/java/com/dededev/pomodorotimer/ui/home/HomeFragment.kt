package com.dededev.pomodorotimer.ui.home

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dededev.pomodorotimer.databinding.FragmentHomeBinding
import com.dededev.pomodorotimer.R
import com.dededev.pomodorotimer.service.PomodoroService
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var progressAnimator: ObjectAnimator
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        progressBar = binding.pbTimeProgress

        setupViewModels()

        setupButtonListeners()

        return root
    }

    private fun setupViewModels() {
        // Setup progress bar animation
        homeViewModel.initialTime.observe(viewLifecycleOwner) {initialTime ->
            setupProgressBar(initialTime)
            setupProgressAnimator(initialTime)

            if (homeViewModel.isTimerRunning.value == true) {
                updateProgressAnimator(initialTime)
            }
        }

        // Observe Timer Type: FOCUS / BREAK
        homeViewModel.currentTimerType.observe(viewLifecycleOwner) {
            binding.textView2.text = it.toString()
        }

        // Observe Session Counts
        homeViewModel.sessionCount.observe(viewLifecycleOwner) { sessionCount ->
            binding.tvSessionCount.text =
                getString(R.string.session_text, sessionCount, homeViewModel.sessionTotal)
        }

        // Update the timer UI
        homeViewModel.timeLeftInMillis.observe(viewLifecycleOwner) {timeLeft ->
            updateTimer(binding.homeTimer, timeLeft)
        }

        // Handling start / pause button icon & progress bar animation
        homeViewModel.isTimerRunning.observe(viewLifecycleOwner) {isRunning ->
            handleTimerState(isRunning)
        }
    }

    private fun setupButtonListeners() {
        binding.btnTimerPlay.setOnClickListener {
//            if (homeViewModel.isTimerRunning.value == true) {
//                homeViewModel.pauseTimer()
//            } else {
//                homeViewModel.startTimer()
//            }
            startPomodoroService()
        }

        binding.btnTimerReset.setOnClickListener { homeViewModel.resetTimer() }

        binding.btnTimerNext.setOnClickListener { homeViewModel.skipToNextPhase() }
    }

    private fun startPomodoroService() {
        val pomodoroService = Intent(requireContext(), PomodoroService::class.java)
        pomodoroService.putExtra(PomodoroService.TIMER_ACTION, PomodoroService.START)
        requireContext().startService(pomodoroService)
    }

    private fun setupProgressBar(initialTime: Long) {
        progressBar.apply {
            max = initialTime.toInt()
            progress = initialTime.toInt()
        }
    }

    private fun setupProgressAnimator(initialTime: Long) {
        progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", initialTime.toInt() , 0)
            .setDuration(initialTime)
        progressAnimator.interpolator = LinearInterpolator()
    }

    // ProgressBar Animation Progress for OnCreateFragment (Fragment Initiation or Reopen)
    private fun updateProgressAnimator(initialTime: Long) {
        var timeLeft = homeViewModel.timeLeftInMillis.value ?: return

        // timeLeft == 0 meaning the timer ready to next session; timeLeft reset to initialTime
        if (timeLeft.toInt() == 0) timeLeft = initialTime

        progressBar.progress = timeLeft.toInt()
        progressAnimator.apply {
            setIntValues((timeLeft).toInt(), 0)
            duration = timeLeft
        }.start()
    }

    private fun handleTimerState(isRunning: Boolean) {
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