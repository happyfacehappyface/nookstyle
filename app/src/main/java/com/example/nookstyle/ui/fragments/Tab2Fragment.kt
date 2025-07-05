package com.example.nookstyle.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.ui.adapter.ScreenshotAdapter
import com.example.nookstyle.util.LikeCountManager
import java.io.File

class Tab2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: ScreenshotAdapter
    private var screenshotFiles = mutableListOf<File>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // UI 요소 초기화
        recyclerView = view.findViewById(R.id.recyclerViewScreenshots)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        
        // RecyclerView 초기화
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        
        // 성능 최적화 설정
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20) // 캐시 크기 증가
        
        // 스크린샷 파일들 로드
        loadScreenshotFiles()
        
        // 어댑터 설정
        adapter = ScreenshotAdapter(
            screenshotFiles = screenshotFiles,
            onItemClick = { file ->
                // 이미지 클릭 시 전체화면으로 보기 (나중에 구현)
                showFullScreenImage(file)
            },
            onDeleteClick = { file ->
                // 삭제 버튼 클릭 시 확인 다이얼로그 표시
                showDeleteConfirmationDialog(file)
            },
            onSubmitClick = { file ->
                // 출품 버튼 클릭 시 Tab3에 이미지 복사
                submitToContest(file)
            }
        )
        recyclerView.adapter = adapter
        
        // 빈 상태 업데이트
        updateEmptyState()
    }
    
    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때마다 파일 목록 새로고침
        loadScreenshotFiles()
        adapter.updateFiles(screenshotFiles)
        updateEmptyState()
    }
    
    private fun loadScreenshotFiles() {
        try {
            val screenshotsDir = File(requireContext().getExternalFilesDir(null), "Screenshots")
            if (screenshotsDir.exists() && screenshotsDir.isDirectory) {
                screenshotFiles.clear()
                val files = screenshotsDir.listFiles { file ->
                    file.isFile && (file.extension.lowercase() == "jpg" || file.extension.lowercase() == "jpeg" || file.extension.lowercase() == "png")
                }
                files?.sortedByDescending { it.lastModified() }?.let { sortedFiles ->
                    screenshotFiles.addAll(sortedFiles)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun showFullScreenImage(file: File) {
        // TODO: 전체화면 이미지 보기 다이얼로그 구현
        // 현재는 간단한 토스트 메시지로 대체
        Toast.makeText(context, "이미지: ${file.name}", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * 삭제 확인 다이얼로그 표시
     */
    private fun showDeleteConfirmationDialog(file: File) {
        AlertDialog.Builder(requireContext())
            .setTitle("스크린샷 삭제")
            .setMessage("이 스크린샷을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteScreenshot(file)
            }
            .setNegativeButton("취소", null)
            .show()
    }
    
    /**
     * 빈 상태 업데이트
     */
    private fun updateEmptyState() {
        if (screenshotFiles.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
    
    /**
     * 스크린샷 파일 삭제
     */
    private fun deleteScreenshot(file: File) {
        try {
            if (file.exists() && file.delete()) {
                Toast.makeText(context, "스크린샷이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                // 파일 목록 새로고침
                loadScreenshotFiles()
                adapter.updateFiles(screenshotFiles)
                updateEmptyState()
            } else {
                Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 콘테스트에 출품
     */
    private fun submitToContest(file: File) {
        try {
            // contest 폴더에 이미지 복사
            val contestDir = File(requireContext().getExternalFilesDir(null), "contest")
            if (!contestDir.exists()) {
                contestDir.mkdirs()
            }
            
            val contestFile = File(contestDir, file.name)
            file.copyTo(contestFile, overwrite = true)
            
            // 새로운 이미지에 좋아요 수 할당
            val imageName = "external/${file.name}"
            LikeCountManager.addNewImage(imageName)
            
            Toast.makeText(context, "콘테스트에 출품되었습니다!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "출품에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
} 