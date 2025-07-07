package com.example.nookstyle.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.ColorItem

class ColorFilterAdapter(
    private val colors: List<ColorItem>,
    private val onColorSelected: (ColorItem) -> Unit
) : RecyclerView.Adapter<ColorFilterAdapter.ColorViewHolder>() {

    private var selectedColor: ColorItem? = null

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorCircle: View = itemView.findViewById(R.id.colorCircle)
        private val colorCircleContainer: View = itemView.findViewById(R.id.colorCircleContainer)
        private val colorName: TextView = itemView.findViewById(R.id.colorName)

        fun bind(colorItem: ColorItem) {
            colorName.text = colorItem.name
            
            if (colorItem.isClearFilter) {
                // 필터 해제 버튼인 경우
                colorCircle.setBackgroundResource(R.drawable.color_icon)
                colorCircle.alpha = 1.0f
                colorCircleContainer.setBackgroundResource(R.drawable.color_circle_border)
                
                // 선택된 색상 표시 (필터 해제 버튼이 선택된 경우)
                if (selectedColor == colorItem) {
                    colorCircleContainer.setBackgroundResource(R.drawable.color_circle_selected)
                }
            } else {
                // 일반 색상 버튼인 경우
                val circleDrawable = createCircleDrawable(colorItem.color)
                colorCircle.background = circleDrawable
                
                // 선택된 색상 표시
                if (selectedColor == colorItem) {
                    colorCircle.alpha = 0.8f
                    colorCircleContainer.setBackgroundResource(R.drawable.color_circle_selected)
                } else {
                    colorCircle.alpha = 1.0f
                    colorCircleContainer.setBackgroundResource(R.drawable.color_circle_border)
                }
            }
            
            itemView.setOnClickListener {
                selectedColor = colorItem
                onColorSelected(colorItem)
                notifyDataSetChanged()
            }
        }
        
        private fun createCircleDrawable(color: Int): android.graphics.drawable.GradientDrawable {
            return android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(color)
                setSize(44, 44)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount(): Int = colors.size

    fun getSelectedColor(): ColorItem? = selectedColor

    fun setSelectedColor(colorItem: ColorItem) {
        selectedColor = colorItem
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedColor = null
        notifyDataSetChanged()
    }
} 