package com.example.nookstyle.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import java.io.File

class ScreenshotAdapter(
    private var screenshotFiles: List<File>,
    private val onItemClick: (File) -> Unit
) : RecyclerView.Adapter<ScreenshotAdapter.ScreenshotViewHolder>() {

    class ScreenshotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewScreenshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_screenshot, parent, false)
        return ScreenshotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val file = screenshotFiles[position]
        
        // 이미지 로드
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            holder.imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            // 기본 이미지 설정
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        
        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClick(file)
        }
    }

    override fun getItemCount() = screenshotFiles.size
    
    fun updateFiles(newFiles: List<File>) {
        screenshotFiles = newFiles
        notifyDataSetChanged()
    }
} 