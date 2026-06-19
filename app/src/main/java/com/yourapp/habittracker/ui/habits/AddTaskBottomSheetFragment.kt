package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yourapp.habittracker.R
import com.yourapp.habittracker.databinding.FragmentAddTaskBottomSheetBinding

// ← THÊM IMPORT NÀY
import com.yourapp.habittracker.ui.habits.AddTaskFragment

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddTaskBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // ===== CUSTOM TASK CARD - Mở AddTaskFragment =====
        binding.cvCustomTask.setOnClickListener {
            // Đóng Bottom Sheet
            dismiss()
            // Mở AddTaskFragment
            openAddTaskFragment()
        }

        // ===== RECOMMENDED TASKS =====
        binding.cvMeditate.setOnClickListener {
            addTaskToDatabase("Meditate", "🧘", "Mind", true)
        }
        binding.cvPushUps.setOnClickListener {
            addTaskToDatabase("Push-ups", "💪", "Body", true)
        }
        binding.cvReadBooks.setOnClickListener {
            addTaskToDatabase("Read books", "📚", "Learn", true)
        }

        // ===== DISCIPLINE TASKS =====
        binding.llBasketball.setOnClickListener {
            addTaskToDatabase("Basketball practice", "🏀", "Discipline", false)
        }
        binding.llBlockScreens.setOnClickListener {
            addTaskToDatabase("Block screens", "📱", "Discipline", false)
        }
        binding.llBusiness.setOnClickListener {
            addTaskToDatabase("Business", "💼", "Work", false)
        }
        binding.llCardio.setOnClickListener {
            addTaskToDatabase("Cardio exercise", "🏃", "Body", false)
        }

        // ===== CATEGORY CHIPS =====
        setupCategoryChips()
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipAll,
            binding.chipBody,
            binding.chipMind,
            binding.chipLearn,
            binding.chipWork,
            binding.chipDiscipline
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                // Reset all chips
                chips.forEach { c ->
                    c.setTextColor(resources.getColor(android.R.color.black, null))
                    c.setChipBackgroundColorResource(android.R.color.darker_gray)
                }
                // Highlight selected chip
                chip.setTextColor(resources.getColor(android.R.color.white, null))
                chip.setChipBackgroundColorResource(android.R.color.black)

                val category = chip.text.toString()
                Toast.makeText(requireContext(), "Filter: $category", Toast.LENGTH_SHORT).show()
                // TODO: Filter tasks by category
            }
        }
    }

    /**
     * Mở AddTaskFragment
     */
    private fun openAddTaskFragment() {
        try {
            val activity = requireActivity()

            if (activity is androidx.appcompat.app.AppCompatActivity) {
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddTaskFragment.newInstance())
                    .addToBackStack("add_task")
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Cannot open add task", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTaskToDatabase(name: String, icon: String, category: String, isRecommended: Boolean) {
        try {
            if (context == null) {
                Toast.makeText(requireContext(), "Context is null", Toast.LENGTH_SHORT).show()
                return
            }

            val habit = com.yourapp.habittracker.data.local.entity.HabitEntity(
                name = name,
                description = "Habit: $name",
                timeBlock = "The Day",
                xpReward = 25,
                icon = icon,
                colorHex = getColorForCategory(category),
                sortOrder = 0,
                category = category,
                isRecommended = isRecommended
            )

            viewModel.createHabitFromBottomSheet(habit)

            Toast.makeText(requireContext(), "✅ Added: $name", Toast.LENGTH_SHORT).show()
            dismiss()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getColorForCategory(category: String): String {
        return when (category) {
            "Body" -> "#FF8A65"
            "Mind" -> "#81D4FA"
            "Learn" -> "#81C784"
            "Work" -> "#FFB74D"
            "Discipline" -> "#CE93D8"
            "Health" -> "#4FC3F7"
            "Creative" -> "#F06292"
            else -> "#BDBDBD"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = AddTaskBottomSheetFragment()
    }
}