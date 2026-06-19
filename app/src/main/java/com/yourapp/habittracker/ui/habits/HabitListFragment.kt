package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast  // ← Thêm import này
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourapp.habittracker.databinding.FragmentHabitListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HabitListFragment : Fragment() {
    private var _binding: FragmentHabitListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onHabitClick = { habit -> /* TODO: Navigate to Detail */ },
            onCheckClick = { habit -> viewModel.toggleCompletion(habit.id, habit.xpReward) },
            onPartialClick = { habit -> viewModel.markPartial(habit.id, habit.xpReward) },
            onSkipClick = { habit -> viewModel.markSkipped(habit.id) }
        )

        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HabitListFragment.adapter
        }
    }

    private fun observeData() {
        // Observe habits list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeHabits.collectLatest { habits ->
                adapter.submitList(habits)
            }
        }

        // Observe user stats
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userStats.collectLatest { stats ->
                // Dùng giá trị mặc định nếu stats null
                val streak = stats?.currentStreak ?: 0
                val trophy = stats?.achievementPoints ?: 0
                val xp = stats?.totalXp ?: 0
                val day = stats?.challengeDay ?: 1
                val total = stats?.challengeTotal ?: 66

                binding.tvStreakBadge.text = "🔥 $streak"
                binding.tvTrophyBadge.text = "🏆 $trophy"
                binding.tvXpBadge.text = "✦ $xp"
                binding.tvDayNumber.text = "Day $day"
                binding.tvDayTotal.text = "/$total"
            }
        }

        // Observe time blocks
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.timeBlocks.collectLatest { blocks ->
                if (blocks.isNotEmpty()) {
                    binding.tvSectionHeader.text = blocks.firstOrNull() ?: "Tasks"
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Mở Streak Bottom Sheet
        binding.tvStreakBadge.setOnClickListener {
            val bottomSheet = StreakBadgesBottomSheet()
            bottomSheet.show(parentFragmentManager, "StreakBadgesBottomSheet")
        }

        // Mở Add Task Bottom Sheet
        binding.fabAddHabit.setOnClickListener {
            val addTaskSheet = AddTaskBottomSheetFragment.newInstance()
            addTaskSheet.show(parentFragmentManager, "AddTaskBottomSheet")
        }
    }

    // Nhận kết quả từ Bottom Sheet
    override fun onResume() {
        super.onResume()
        parentFragmentManager.setFragmentResultListener("add_task_request", this) { _, bundle ->
            val taskName = bundle.getString("task_name") ?: ""
            val taskIcon = bundle.getString("task_icon") ?: "✅"

            Toast.makeText(requireContext(), "Đã thêm: $taskName $taskIcon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}