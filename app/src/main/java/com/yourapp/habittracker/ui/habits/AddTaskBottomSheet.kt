package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yourapp.habittracker.databinding.FragmentAddTaskBinding

class AddTaskBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()

    private var selectedTimeBlock = "Morning"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Xử lý chọn Time Block (các TextView trong HorizontalScrollView)
        setupTimeBlockSelection()

        // Lưu Task
        binding.btnSaveTask.setOnClickListener {
            val taskName = binding.tilHabitName.editText?.text?.toString()?.trim() ?: ""
            if (taskName.isNotEmpty()) {
                viewModel.createHabit(
                    name = taskName,
                    description = "",
                    timeBlock = selectedTimeBlock,
                    xpReward = 25, // Mặc định
                    icon = "✅",
                    colorHex = "#FF8A65"
                )
                dismiss()
            } else {
                binding.tilHabitName.error = "Please enter a task name"
            }
        }
    }

    private fun setupTimeBlockSelection() {
        val timeBlocks = binding.hsvTimeBlocks.getChildAt(0) as? android.view.ViewGroup
        timeBlocks?.let { linearLayout ->
            for (i in 0 until linearLayout.childCount) {
                val child = linearLayout.getChildAt(i)
                if (child is android.widget.TextView) {
                    child.setOnClickListener {
                        // Reset tất cả
                        for (j in 0 until linearLayout.childCount) {
                            val other = linearLayout.getChildAt(j) as? android.widget.TextView
                            other?.apply {
                                setTextColor(android.graphics.Color.parseColor("#111111"))
                                background.setTint(android.graphics.Color.TRANSPARENT)
                            }
                        }
                        // Highlight selected
                        child.setTextColor(android.graphics.Color.WHITE)
                        child.background.setTint(android.graphics.Color.parseColor("#FF8A65"))
                        selectedTimeBlock = child.text.toString()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}