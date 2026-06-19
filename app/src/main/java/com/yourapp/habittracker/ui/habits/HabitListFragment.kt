package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourapp.habittracker.R
import com.yourapp.habittracker.databinding.FragmentHabitListBinding
import com.yourapp.habittracker.ui.tabs.DoneFragment
import com.yourapp.habittracker.ui.tabs.SkippedFragment
import com.yourapp.habittracker.ui.tabs.TodosFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        parentFragmentManager.setFragmentResultListener("add_task_request", this) { _, bundle ->
            val taskName = bundle.getString("task_name") ?: ""
            val taskIcon = bundle.getString("task_icon") ?: "✅"
            Toast.makeText(requireContext(), "Đã thêm: $taskName $taskIcon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onHabitClick = { habit ->
                // TODO: Navigate to Detail
            },
            onCheckClick = { habit ->
                viewModel.toggleCompletion(habit.id, habit.xpReward)
                updateStats()
            },
            onPartialClick = { habit ->
                viewModel.markPartial(habit.id, habit.xpReward)
                updateStats()
                Toast.makeText(requireContext(), "✅ ${habit.name}: Hoàn thành một phần", Toast.LENGTH_SHORT).show()
            },
            onSkipClick = { habit ->
                viewModel.markSkipped(habit.id)
                updateStats()
                Toast.makeText(requireContext(), "⏭️ ${habit.name}: Đã bỏ qua", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HabitListFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeHabits.collectLatest { habits ->
                adapter.submitList(habits)
                updateStats()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userStats.collectLatest { stats ->
                val streak = stats?.currentStreak ?: 0
                val trophy = stats?.achievementPoints ?: 0
                val xp = stats?.totalXp ?: 0
                val day = stats?.challengeDay ?: 1
                val total = stats?.challengeTotal ?: 66

                binding.tvStreakBadge.text = "🔥 $streak"
                binding.tvTrophyBadge.text = "🏆 $trophy"
                binding.tvXpBadge.text = "✦ $xp"
                binding.tvDayNumber.text = "DAY $day"
                binding.tvDayTotal.text = "/$total"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.timeBlocks.collectLatest { blocks ->
                if (blocks.isNotEmpty()) {
                    binding.tvSectionHeader.text = blocks.firstOrNull() ?: "The Day"
                }
            }
        }
    }

    private fun updateStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val habits = viewModel.activeHabits.value
                val totalTodos = habits.size

                var doneCount = 0
                var skippedCount = 0

                for (habit in habits) {
                    val log = viewModel.getLogByDate(habit.id, today)
                    if (log != null) {
                        when (log.status) {
                            "completed", "partial" -> doneCount++
                            "skipped" -> skippedCount++
                            else -> { /* Bỏ qua */ }
                        }
                    }
                }

                val todosCount = totalTodos - (doneCount + skippedCount)

                binding.tvTodosCount.text = todosCount.toString()
                binding.tvDoneCount.text = doneCount.toString()
                binding.tvSkippedCount.text = skippedCount.toString()

            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvTodosCount.text = "0"
                binding.tvDoneCount.text = "0"
                binding.tvSkippedCount.text = "0"
            }
        }
    }

    private fun setupClickListeners() {
        // ===== CÁCH 1: Click trên toàn bộ LinearLayout =====
        // Lấy các LinearLayout chứa từng ô
        val llTodos = binding.llStatsRow.getChildAt(0) as? View
        val llDone = binding.llStatsRow.getChildAt(2) as? View
        val llSkipped = binding.llStatsRow.getChildAt(4) as? View

        // Click To-dos
        llTodos?.setOnClickListener {
            Toast.makeText(requireContext(), "📋 Đã click To-dos", Toast.LENGTH_SHORT).show()
            openTabFragment(TodosFragment.newInstance(), "To-dos")
        }

        // Click Done
        llDone?.setOnClickListener {
            Toast.makeText(requireContext(), "✅ Đã click Done", Toast.LENGTH_SHORT).show()
            openTabFragment(DoneFragment.newInstance(), "Done")
        }

        // Click Skipped
        llSkipped?.setOnClickListener {
            Toast.makeText(requireContext(), "⏭️ Đã click Skipped", Toast.LENGTH_SHORT).show()
            openTabFragment(SkippedFragment.newInstance(), "Skipped")
        }

        // ===== CÁCH 2: Click trên TextView số =====
        binding.tvTodosCount.setOnClickListener {
            Toast.makeText(requireContext(), "📋 Đã click To-dos", Toast.LENGTH_SHORT).show()
            openTabFragment(TodosFragment.newInstance(), "To-dos")
        }

        binding.tvDoneCount.setOnClickListener {
            Toast.makeText(requireContext(), "✅ Đã click Done", Toast.LENGTH_SHORT).show()
            openTabFragment(DoneFragment.newInstance(), "Done")
        }

        binding.tvSkippedCount.setOnClickListener {
            Toast.makeText(requireContext(), "⏭️ Đã click Skipped", Toast.LENGTH_SHORT).show()
            openTabFragment(SkippedFragment.newInstance(), "Skipped")
        }

        binding.tvStreakBadge.setOnClickListener {
            val bottomSheet = StreakBadgesBottomSheet()
            bottomSheet.show(parentFragmentManager, "StreakBadgesBottomSheet")
        }

        binding.fabAddHabit.setOnClickListener {
            val addTaskSheet = AddTaskBottomSheetFragment.newInstance()
            addTaskSheet.show(parentFragmentManager, "AddTaskBottomSheet")
        }
    }

    private fun openTabFragment(fragment: Fragment, title: String) {
        try {
            if (context == null) {
                Toast.makeText(requireContext(), "Context is null", Toast.LENGTH_SHORT).show()
                return
            }

            val bundle = Bundle().apply {
                putString("tab_title", title)
            }
            fragment.arguments = bundle

            val activity = requireActivity()
            if (activity is androidx.appcompat.app.AppCompatActivity) {
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("tab_$title")
                    .commit()

                Toast.makeText(requireContext(), "Đang mở: $title", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Activity không phải AppCompatActivity", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}