package com.yourapp.habittracker.ui.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourapp.habittracker.databinding.FragmentTodosBinding
import com.yourapp.habittracker.ui.habits.HabitAdapter
import com.yourapp.habittracker.ui.habits.HabitViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodosFragment : Fragment() {

    private var _binding: FragmentTodosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodosBinding.inflate(inflater, container, false)
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

        binding.rvTodos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TodosFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

            viewModel.activeHabits.collectLatest { habits ->
                val todosList = habits.filter { habit ->
                    val log = viewModel.getLogByDate(habit.id, today)
                    log == null || log.status == "pending" || log.status == "missed"
                }
                adapter.submitList(todosList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = TodosFragment()
    }
}