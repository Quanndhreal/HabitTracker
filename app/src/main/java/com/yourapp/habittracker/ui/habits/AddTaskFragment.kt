package com.yourapp.habittracker.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.yourapp.habittracker.R
import com.yourapp.habittracker.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HabitViewModel by viewModels()

    private var selectedIcon: String = "✅"
    private var selectedTimeBlock: String = "Buổi sáng"
    private lateinit var iconAdapter: IconPickerAdapter

    private val iconList = listOf(
        "✅", "💪", "🧘", "📚", "🏃", "💧",
        "📖", "🎯", "🎨", "🎵", "🌅", "🌙",
        "⭐", "🔥", "💡", "🌈", "🌸", "🍀"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTimeBlocks()
        setupIconPicker()
        setupSaveButton()
    }

    private fun setupTimeBlocks() {
        val timeBlocks = listOf("🌅 Sáng sớm", "☀️ Buổi sáng", "🌤️ Buổi trưa",
            "🌇 Chiều muộn", "🌅 Buổi tối", "🌙 Đêm khuya")

        val container = binding.llTimeBlockContainer
        container.removeAllViews()

        timeBlocks.forEachIndexed { index, timeBlock ->
            val tv = android.widget.TextView(requireContext()).apply {
                text = timeBlock
                setPadding(24, 12, 24, 12)
                background = resources.getDrawable(R.drawable.bg_round_stats, null)
                background.setTint(if (index == 0) android.graphics.Color.parseColor("#FF8A65") else android.graphics.Color.TRANSPARENT)
                setTextColor(if (index == 0) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#111111"))
                textSize = 14f
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    for (i in 0 until container.childCount) {
                        val child = container.getChildAt(i) as? android.widget.TextView
                        child?.apply {
                            background.setTint(android.graphics.Color.TRANSPARENT)
                            setTextColor(android.graphics.Color.parseColor("#111111"))
                        }
                    }
                    background.setTint(android.graphics.Color.parseColor("#FF8A65"))
                    setTextColor(android.graphics.Color.WHITE)
                    selectedTimeBlock = text.toString()
                }
            }
            container.addView(tv)
        }
    }

    private fun setupIconPicker() {
        iconAdapter = IconPickerAdapter(iconList) { icon ->
            selectedIcon = icon
            iconAdapter.notifyDataSetChanged()
        }

        binding.rvIcons.apply {
            layoutManager = GridLayoutManager(requireContext(), 6)
            adapter = iconAdapter
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveTask.setOnClickListener {
            val name = binding.etTaskName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            if (name.isEmpty()) {
                binding.tilHabitName.error = "Vui lòng nhập tên thói quen"
                return@setOnClickListener
            }

            viewModel.createHabit(
                name = name,
                description = description,
                timeBlock = selectedTimeBlock,
                xpReward = 25,
                icon = selectedIcon,
                colorHex = "#FF8A65"
            )

            Toast.makeText(requireContext(), "✅ Đã thêm: $name", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    inner class IconPickerAdapter(
        private val icons: List<String>,
        private val onIconClick: (String) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<IconPickerAdapter.IconViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon_picker, parent, false)
            return IconViewHolder(view)
        }

        override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
            val icon = icons[position]
            holder.bind(icon, icon == selectedIcon)
        }

        override fun getItemCount() = icons.size

        inner class IconViewHolder(itemView: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

            private val tvIcon: android.widget.TextView = itemView.findViewById(R.id.tvIcon)

            fun bind(icon: String, isSelected: Boolean) {
                tvIcon.text = icon
                tvIcon.background.setTint(
                    if (isSelected) android.graphics.Color.parseColor("#FF8A65")
                    else android.graphics.Color.parseColor("#E0E0E0")
                )
                tvIcon.setOnClickListener {
                    onIconClick(icon)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): AddTaskFragment {
            return AddTaskFragment()
        }
    }
}