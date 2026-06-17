package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yourapp.habittracker.databinding.FragmentStreakBadgesBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StreakBadgesBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentStreakBadgesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreakBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userStats.collectLatest { stats ->
                stats?.let {
                    binding.tvBigStreak.text = it.currentStreak.toString()
                    binding.tvStreakSubtitle.text = "day streak"

                    binding.tvStreakFreeze.apply {
                        text = "Streak Freeze 🧊"
                        alpha = if (it.streakFreezesAvailable > 0) 1f else 0.3f
                    }

                    binding.tvChallengeDay.text = "Day ${it.challengeDay} of ${it.challengeTotal}"
                }
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}