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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.ContestImage
import com.example.nookstyle.model.ReportData
import com.example.nookstyle.model.ReportReason
import com.example.nookstyle.util.LikeCountManager
import com.example.nookstyle.util.ReportManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ContestImageAdapter(
    private var contestImages: List<ContestImage>,
    private val onItemClick: (String) -> Unit,
    private val onCancelSubmission: (ContestImage) -> Unit
) : RecyclerView.Adapter<ContestImageAdapter.ContestImageViewHolder>() {

    class ContestImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewContest)
        val tvFileName: TextView = view.findViewById(R.id.tvFileName)
        val tvMySubmission: TextView = view.findViewById(R.id.tvMySubmission)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
        val btnKebabMenu: ImageView = view.findViewById(R.id.btnKebabMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contest_image, parent, false)
        return ContestImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContestImageViewHolder, position: Int) {
        val contestImage = contestImages[position]
        val imageName = contestImage.imageName
        
        // 파일명 표시 (경로와 확장자 제외)
        val fileName = if (imageName.startsWith("assets/")) {
            // assets/filename.jpg -> filename
            imageName.substringAfter("assets/").substringBeforeLast(".")
        } else {
            // external/filename.jpg -> filename
            imageName.substringAfter("external/").substringBeforeLast(".")
        }
        holder.tvFileName.text = fileName
        
        // 좋아요 상태 설정
        updateLikeButton(holder, contestImage)
        
        // 출품 표시 배지 표시 여부 설정
        if (contestImage.isSubmitted) {
            holder.tvMySubmission.visibility = View.VISIBLE
        } else {
            holder.tvMySubmission.visibility = View.GONE
        }
        
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
        
        // 케밥 메뉴 버튼 클릭 리스너
        holder.btnKebabMenu.setOnClickListener {
            showKebabMenu(holder.itemView.context, holder.btnKebabMenu, contestImage, position)
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
            holder.btnLike.setImageResource(R.drawable.ic_heart_filled)
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        } else {
            holder.btnLike.setImageResource(R.drawable.ic_heart_outline)
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light))
        }
    }
    
    /**
     * 좋아요 토글
     */
    private fun toggleLike(position: Int) {
        val contestImage = contestImages[position]
        
        // LikeCountManager를 통해 좋아요 토글
        val newLikeCount = LikeCountManager.toggleLike(contestImage.imageName)
        
        // ContestImage 객체 업데이트
        contestImage.isLiked = LikeCountManager.getLikeState(contestImage.imageName)
        contestImage.likeCount = newLikeCount
        
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
     * 케밥 메뉴 표시
     */
    private fun showKebabMenu(context: Context, anchorView: View, contestImage: ContestImage, position: Int) {
        val popupMenu = PopupMenu(context, anchorView)
        
        // 내 출품 이미지인지 확인하여 다른 메뉴 표시
        if (contestImage.isSubmitted) {
            // 내 출품 이미지: 저장하기, 삭제
            popupMenu.menuInflater.inflate(R.menu.contest_image_menu_my_submission, popupMenu.menu)
        } else {
            // 일반 이미지: 저장하기, 신고
            popupMenu.menuInflater.inflate(R.menu.contest_image_menu, popupMenu.menu)
        }
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_save -> {
                    saveToGallery(context, contestImage.imageName)
                    true
                }
                R.id.action_report -> {
                    showReportDialog(context, contestImage)
                    true
                }
                R.id.action_delete -> {
                    onCancelSubmission(contestImage)
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }
    
    /**
     * 신고 다이얼로그 표시
     */
    private fun showReportDialog(context: Context, contestImage: ContestImage) {
        // ReportManager 초기화
        ReportManager.init(context)
        
        // 이미 신고된 이미지인지 확인
        if (ReportManager.isReported(contestImage.imageName)) {
            Toast.makeText(context, "이미 신고된 이미지입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 신고 사유 선택 다이얼로그 생성
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report_reason, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupReportReason)
        val etOtherReason = dialogView.findViewById<EditText>(R.id.etOtherReason)
        
        // "기타" 선택 시 텍스트 입력 필드 표시
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            etOtherReason.visibility = if (checkedId == R.id.rbOther) View.VISIBLE else View.GONE
        }
        
        val dialog = android.app.AlertDialog.Builder(context)
            .setTitle("신고 사유 선택")
            .setView(dialogView)
            .setPositiveButton("신고") { _, _ ->
                val selectedReason = when (radioGroup.checkedRadioButtonId) {
                    R.id.rbInappropriate -> ReportReason.INAPPROPRIATE
                    R.id.rbCopyright -> ReportReason.COPYRIGHT
                    R.id.rbSpam -> ReportReason.SPAM
                    R.id.rbViolence -> ReportReason.VIOLENCE
                    R.id.rbOther -> ReportReason.OTHER
                    else -> ReportReason.INAPPROPRIATE
                }
                
                val otherReason = if (selectedReason == ReportReason.OTHER) {
                    etOtherReason.text.toString().takeIf { it.isNotBlank() }
                } else null
                
                // 신고 데이터 저장
                val reportData = ReportData(
                    imageName = contestImage.imageName,
                    reportReason = selectedReason.displayName,
                    otherReason = otherReason
                )
                
                ReportManager.saveReport(reportData)
                
                val message = if (otherReason != null) {
                    "신고가 접수되었습니다. (사유: $otherReason)"
                } else {
                    "신고가 접수되었습니다. (사유: ${selectedReason.displayName})"
                }
                
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("취소", null)
            .create()
        
        // 다이얼로그 크기 조정
        dialog.setOnShowListener {
            dialog.window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        dialog.show()
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