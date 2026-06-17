package com.yourapp.habittracker.ui.detail

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.yourapp.habittracker.R
import com.yourapp.habittracker.data.local.entity.HabitLogEntity

class HeatmapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private val cellSize = 40

    fun setData(logs: List<HabitLogEntity>, weeks: Int = 4) {
        removeAllViews()

        columnCount = 7
        rowCount = weeks

        // Tạo map để tra nhanh
        val logMap = logs.associateBy { it.date }

        // Tạo cells
        val today = java.time.LocalDate.now()
        val startDate = today.minusWeeks(weeks.toLong())
        val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

        var currentDate = startDate
        while (!currentDate.isAfter(today)) {
            val dateStr = currentDate.format(formatter)
            val log = logMap[dateStr]

            val cellView = TextView(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams(cellSize, cellSize)
                ).apply {
                    setMargins(4, 4, 4, 4)
                }
                gravity = Gravity.CENTER
                background = context.getDrawable(R.drawable.bg_round_stats)

                // Màu sắc dựa trên trạng thái
                background.setTint(when (log?.status) {
                    "completed" -> Color.parseColor("#216E39")
                    "partial" -> Color.parseColor("#9BE9A8")
                    "skipped" -> Color.parseColor("#FF8A65")
                    else -> Color.parseColor("#EBEBEB")
                })

                text = when (log?.status) {
                    "completed" -> "✓"
                    "partial" -> "◐"
                    else -> ""
                }
                setTextColor(Color.WHITE)
                textSize = 12f
            }

            addView(cellView)
            currentDate = currentDate.plusDays(1)
        }
    }
}