package com.example.nookstyle.ui.adapter

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ScreenshotAdapter(
    private var screenshotFiles: List<File>,
    private val onItemClick: (File) -> Unit,
    private val onDeleteClick: (File) -> Unit,
    private val onSubmitClick: (File) -> Unit
) : RecyclerView.Adapter<ScreenshotAdapter.ScreenshotViewHolder>() {

    class ScreenshotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewScreenshot)
        val tvFileName: TextView = view.findViewById(R.id.tvFileName)
        val btnSaveToGallery: Button = view.findViewById(R.id.btnSaveToGallery)
        val btnSubmit: Button = view.findViewById(R.id.btnSubmit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_screenshot, parent, false)
        return ScreenshotViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val file = screenshotFiles[position]
        
        // 파일명 표시 (확장자 제외)
        val fileName = file.nameWithoutExtension
        holder.tvFileName.text = fileName
        
        // 이미지 로드 (메모리 최적화)
        try {
            // BitmapFactory.Options를 사용하여 메모리 사용량 최적화
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            // 이미지 크기 계산
            val maxSize = 512 // 최대 크기 제한
            var inSampleSize = 1
            if (options.outHeight > maxSize || options.outWidth > maxSize) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxSize && halfWidth / inSampleSize >= maxSize) {
                    inSampleSize *= 2
                }
            }
            
            // 실제 디코딩
            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.RGB_565 // 메모리 사용량 절약
            }
            
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
            holder.imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            // 기본 이미지 설정
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        
        // 이미지 클릭 리스너 설정
        holder.imageView.setOnClickListener {
            onItemClick(file)
        }
        
        // 갤러리 저장 버튼 클릭 리스너
        holder.btnSaveToGallery.setOnClickListener {
            saveToGallery(holder.itemView.context, file)
        }
        
        // 출품 버튼 클릭 리스너
        holder.btnSubmit.setOnClickListener {
            onSubmitClick(file)
        }
        
        // 삭제 버튼 클릭 리스너
        holder.btnDelete.setOnClickListener {
            onDeleteClick(file)
        }
    }

    override fun getItemCount() = screenshotFiles.size
    
    fun updateFiles(newFiles: List<File>) {
        screenshotFiles = newFiles
        notifyDataSetChanged()
    }
    
    /**
     * 이미지를 갤러리에 저장
     */
    private fun saveToGallery(context: Context, file: File) {
        try {
            // MediaStore를 사용하여 갤러리에 저장
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            uri?.let { imageUri ->
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    FileInputStream(file).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Toast.makeText(context, "갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "갤러리 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
} 