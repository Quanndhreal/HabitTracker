package com.yourapp.habittracker.ui.statistics

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.yourapp.habittracker.R

class WeeklyProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val dayLabels = arrayOf("S", "M", "T", "W", "T", "F", "S")
    private val dayViews = mutableListOf<TextView>()

    init {
        orientation = VERTICAL

        // Row: Circles
        val circlesRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }
        addView(circlesRow)

        // Row: Labels
        val labelsRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }
        addView(labelsRow)

        for (i in 0..6) {
            val circle = TextView(context).apply {
                layoutParams = LayoutParams(48, 48).apply {
                    setMargins(8, 0, 8, 0)
                }
                background = context.getDrawable(R.drawable.bg_round_stats)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = 12f
            }
            circlesRow.addView(circle)

            val label = TextView(context).apply {
                layoutParams = LayoutParams(48, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(8, 8, 8, 0)
                }
                text = dayLabels[i]
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#888888"))
                textSize = 14f
            }
            labelsRow.addView(label)

            dayViews.add(circle)
        }
    }

    fun setData(progress: List<Int>) {
        for (i in 0..6) {
            val status = progress.getOrElse(i) { 0 }
            dayViews[i].apply {
                when {
                    status > 0 -> {
                        background.setTint(Color.parseColor("#FF4500"))
                        text = "✓"
                    }
                    else -> {
                        background.setTint(Color.parseColor("#333333"))
                        text = ""
                    }
                }
            }
        }
    }
}