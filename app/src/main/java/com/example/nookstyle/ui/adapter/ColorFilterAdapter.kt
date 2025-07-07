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
        private val colorName: TextView = itemView.findViewById(R.id.colorName)

        fun bind(colorItem: ColorItem) {
            colorName.text = colorItem.name
            
            // 색상 원형 배경 설정
            colorCircle.setBackgroundColor(colorItem.color)
            
            // 선택된 색상 표시
            if (selectedColor == colorItem) {
                colorCircle.alpha = 0.7f
            } else {
                colorCircle.alpha = 1.0f
            }
            
            itemView.setOnClickListener {
                selectedColor = colorItem
                onColorSelected(colorItem)
                notifyDataSetChanged()
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

    fun clearSelection() {
        selectedColor = null
        notifyDataSetChanged()
    }
} 