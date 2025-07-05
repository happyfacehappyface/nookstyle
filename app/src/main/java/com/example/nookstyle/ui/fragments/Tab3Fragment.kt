package com.example.nookstyle.ui.fragments

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
import com.example.nookstyle.ui.adapter.ContestImageAdapter
import com.example.nookstyle.model.ContestImage
import java.io.File
import java.io.IOException
import kotlin.random.Random

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
                val randomLikeCount = Random.nextInt(0, 100) // 0~99 사이의 임의 좋아요 수
                contestImages.add(ContestImage(imageName = "assets/$fileName", likeCount = randomLikeCount))
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
                    val randomLikeCount = Random.nextInt(0, 100) // 0~99 사이의 임의 좋아요 수
                    contestImages.add(ContestImage(imageName = "external/${file.name}", likeCount = randomLikeCount, file = file))
                }
            }
            
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    private fun showFullScreenImage(imageName: String) {
        // TODO: 전체화면 이미지 보기 다이얼로그 구현
        // 현재는 간단한 토스트 메시지로 대체
        Toast.makeText(context, "이미지: $imageName", Toast.LENGTH_SHORT).show()
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
} 