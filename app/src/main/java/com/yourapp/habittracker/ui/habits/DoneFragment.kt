package com.yourapp.habittracker.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourapp.habittracker.databinding.FragmentDoneBinding
import com.yourapp.habittracker.ui.habits.HabitAdapter
import com.yourapp.habittracker.ui.habits.HabitViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onHabitClick = { habit -> /* TODO */ },
            onCheckClick = { habit ->
                viewModel.toggleCompletion(habit.id, habit.xpReward)
            },
            onPartialClick = { habit ->
                viewModel.markPartial(habit.id, habit.xpReward)
            },
            onSkipClick = { habit ->
                viewModel.markSkipped(habit.id)
            }
        )

        binding.rvDone.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DoneFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            viewModel.activeHabits.collectLatest { habits ->
                // ✅ CHỈ HIỂN THỊ HABITS ĐÃ HOÀN THÀNH (completed hoặc partial)
                val doneList = habits.filter { habit ->
                    val log = viewModel.getLogByDate(habit.id, today)
                    log != null && (log.status == "completed" || log.status == "partial")
                }
                adapter.submitList(doneList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DoneFragment()
    }
}