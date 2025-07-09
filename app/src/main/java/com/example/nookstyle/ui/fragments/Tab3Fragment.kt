package com.example.nookstyle.ui.fragments

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.ui.adapter.ContestImageAdapter
import com.example.nookstyle.model.ContestImage
import com.example.nookstyle.util.LikeCountManager
import java.io.File
import java.io.IOException

class Tab3Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: ContestImageAdapter
    private var contestImages = mutableListOf<ContestImage>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // UI 요소 초기화
        recyclerView = view.findViewById(R.id.recyclerViewContest)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        
        // RecyclerView 초기화
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        
        // 성능 최적화 설정
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20) // 캐시 크기 증가
        
        // 콘테스트 이미지 파일들 로드
        loadContestImages()
        
        // 어댑터 설정
        adapter = ContestImageAdapter(
            contestImages = contestImages,
            onItemClick = { imageName ->
                // 이미지 클릭 시 전체화면으로 보기 (나중에 구현)
                showFullScreenImage(imageName)
            },
            onCancelSubmission = { contestImage ->
                // 출품 취소 버튼 클릭 시
                cancelSubmission(contestImage)
            }
        )
        recyclerView.adapter = adapter
        
        // 빈 상태 업데이트
        updateEmptyState()
    }
    
    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때마다 파일 목록 새로고침
        loadContestImages()
        adapter.updateImages(contestImages)
        updateEmptyState()
    }
    
    private fun loadContestImages() {
        try {
            contestImages.clear()
            
            // 1. assets/contest 폴더에서 이미지 로드
            val assetManager = requireContext().assets
            val assetFiles = assetManager.list("contest")
            
            assetFiles?.filter { fileName ->
                fileName.lowercase().endsWith(".jpg") || 
                fileName.lowercase().endsWith(".jpeg") || 
                fileName.lowercase().endsWith(".png") ||
                fileName.lowercase().endsWith(".webp")
            }?.forEach { fileName ->
                val imageName = "assets/$fileName"
                val likeCount = LikeCountManager.getLikeCount(imageName)
                val isLiked = LikeCountManager.getLikeState(imageName)
                contestImages.add(ContestImage(imageName = imageName, likeCount = likeCount, isLiked = isLiked))
            }
            
            // 2. 외부 저장소 contest 폴더에서 이미지 로드
            val contestDir = File(requireContext().getExternalFilesDir(null), "contest")
            if (contestDir.exists() && contestDir.isDirectory) {
                val externalFiles = contestDir.listFiles { file ->
                    file.isFile && (file.extension.lowercase() == "jpg" || 
                                   file.extension.lowercase() == "jpeg" || 
                                   file.extension.lowercase() == "png" ||
                                   file.extension.lowercase() == "webp")
                }
                
                externalFiles?.sortedByDescending { it.lastModified() }?.forEach { file ->
                    val imageName = "external/${file.name}"
                    val likeCount = LikeCountManager.getLikeCount(imageName)
                    val isLiked = LikeCountManager.getLikeState(imageName)
                    contestImages.add(ContestImage(imageName = imageName, likeCount = likeCount, file = file, isSubmitted = true, isLiked = isLiked))
                }
            }
            
            // 3. 내 작품 우선, 그 다음 좋아요 수 순으로 정렬
            contestImages.sortWith(compareByDescending<ContestImage> { it.isSubmitted }
                .thenByDescending { it.likeCount })
            
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    private fun showFullScreenImage(imageName: String) {
        try {
            // 전체화면 다이얼로그 레이아웃 인플레이트
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_fullscreen_image, null)
            val fullscreenImageView = dialogView.findViewById<ImageView>(R.id.fullscreenImageView)
            val btnClose = dialogView.findViewById<View>(R.id.btnClose)
            val tvImageTitle = dialogView.findViewById<TextView>(R.id.tvImageTitle)
            
            // 이미지 로드 (assets 또는 외부 파일)
            val bitmap = if (imageName.startsWith("assets/")) {
                // assets에서 로드
                val assetPath = imageName.substringAfter("assets/")
                val inputStream = requireContext().assets.open("contest/$assetPath")
                BitmapFactory.decodeStream(inputStream).also { inputStream.close() }
            } else {
                // 외부 파일에서 로드
                val filePath = imageName.substringAfter("external/")
                val file = File(requireContext().getExternalFilesDir(null), "contest/$filePath")
                BitmapFactory.decodeFile(file.absolutePath)
            }
            
            fullscreenImageView.setImageBitmap(bitmap)
            
            // 이미지 제목 설정 (파일명에서 확장자 제거)
            val fileName = if (imageName.startsWith("assets/")) {
                imageName.substringAfter("assets/").substringBeforeLast(".")
            } else {
                imageName.substringAfter("external/").substringBeforeLast(".")
            }
            tvImageTitle.text = fileName
            
            // 다이얼로그 생성
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            
            // 닫기 버튼 클릭 리스너
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            
            // 배경 클릭 시에도 닫기
            dialogView.setOnClickListener {
                dialog.dismiss()
            }
            
            // 다이얼로그 표시 (전체화면)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog.show()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 빈 상태 업데이트
     */
    private fun updateEmptyState() {
        if (contestImages.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    /**
     * 출품 취소
     */
    private fun cancelSubmission(contestImage: ContestImage) {
        try {
            // 확인 다이얼로그 표시
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("출품 취소")
                .setMessage("이 작품의 출품을 취소하시겠습니까?")
                .setNegativeButton("아니오", null)
                .setPositiveButton("예") { _, _ ->
                    // 파일 삭제
                    contestImage.file?.let { file ->
                        if (file.exists() && file.delete()) {
                            // 좋아요 수 제거
                            LikeCountManager.removeImage(contestImage.imageName)
                            
                            Toast.makeText(context, "출품이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                            // 목록 새로고침
                            loadContestImages()
                            adapter.updateImages(contestImages)
                            updateEmptyState()
                        } else {
                            Toast.makeText(context, "출품 취소에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "출품 취소 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
} 