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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.ContestImage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ContestImageAdapter(
    private var contestImages: List<ContestImage>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ContestImageAdapter.ContestImageViewHolder>() {

    class ContestImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewContest)
        val tvFileName: TextView = view.findViewById(R.id.tvFileName)
        val btnLike: Button = view.findViewById(R.id.btnLike)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
        val btnSaveToGallery: Button = view.findViewById(R.id.btnSaveToGallery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contest_image, parent, false)
        return ContestImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContestImageViewHolder, position: Int) {
        val contestImage = contestImages[position]
        val imageName = contestImage.imageName
        
        // 파일명 표시 (확장자 제외)
        val fileName = imageName.substringBeforeLast(".")
        holder.tvFileName.text = fileName
        
        // 좋아요 상태 설정
        updateLikeButton(holder, contestImage)
        
        // 이미지 로드 (assets 또는 외부 파일)
        try {
            val context = holder.itemView.context
            val bitmap: Bitmap?
            
            if (contestImage.file != null) {
                // 외부 파일에서 로드
                bitmap = loadBitmapFromFile(contestImage.file)
            } else {
                // assets에서 로드
                val assetImageName = imageName.removePrefix("assets/")
                bitmap = loadBitmapFromAssets(context, assetImageName)
            }
            
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap)
            } else {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 기본 이미지 설정
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        
        // 이미지 클릭 리스너 설정
        holder.imageView.setOnClickListener {
            onItemClick(imageName)
        }
        
        // 좋아요 버튼 클릭 리스너
        holder.btnLike.setOnClickListener {
            toggleLike(position)
        }
        
        // 갤러리 저장 버튼 클릭 리스너
        holder.btnSaveToGallery.setOnClickListener {
            saveToGallery(holder.itemView.context, imageName)
        }
    }

    override fun getItemCount() = contestImages.size
    
    fun updateImages(newImages: List<ContestImage>) {
        contestImages = newImages
        notifyDataSetChanged()
    }
    
    /**
     * 좋아요 버튼 상태 업데이트
     */
    private fun updateLikeButton(holder: ContestImageViewHolder, contestImage: ContestImage) {
        holder.tvLikeCount.text = contestImage.likeCount.toString()
        
        if (contestImage.isLiked) {
            holder.btnLike.text = "❤️"
            holder.btnLike.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        } else {
            holder.btnLike.text = "🤍"
            holder.btnLike.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light))
        }
    }
    
    /**
     * 좋아요 토글
     */
    private fun toggleLike(position: Int) {
        val contestImage = contestImages[position]
        contestImage.isLiked = !contestImage.isLiked
        
        if (contestImage.isLiked) {
            contestImage.likeCount++
        } else {
            contestImage.likeCount--
        }
        
        // 해당 아이템만 업데이트
        notifyItemChanged(position)
    }
    
    /**
     * assets에서 비트맵 로드
     */
    private fun loadBitmapFromAssets(context: Context, imageName: String): Bitmap? {
        return try {
            val inputStream: InputStream = context.assets.open("contest/$imageName")
            
            // BitmapFactory.Options를 사용하여 메모리 사용량 최적화
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            
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
            
            val decodeInputStream: InputStream = context.assets.open("contest/$imageName")
            val bitmap = BitmapFactory.decodeStream(decodeInputStream, null, decodeOptions)
            decodeInputStream.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 파일에서 비트맵 로드
     */
    private fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
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
            
            BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 이미지를 갤러리에 저장
     */
    private fun saveToGallery(context: Context, imageName: String) {
        try {
            val contestImage = contestImages.find { it.imageName == imageName }
            val bitmap: Bitmap?
            
            if (contestImage?.file != null) {
                // 외부 파일에서 읽기
                bitmap = loadBitmapFromFile(contestImage.file)
            } else {
                // assets에서 읽기
                val assetImageName = imageName.removePrefix("assets/")
                bitmap = loadBitmapFromAssets(context, assetImageName)
            }
            
            if (bitmap != null) {
                // MediaStore를 사용하여 갤러리에 저장
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, imageName.substringAfterLast("/"))
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                
                uri?.let { imageUri ->
                    resolver.openOutputStream(imageUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    Toast.makeText(context, "갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "갤러리 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
} 