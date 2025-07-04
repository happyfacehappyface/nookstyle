package com.example.nookstyle.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.ui.adapter.ScreenshotAdapter
import java.io.File

class Tab2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
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
        
        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerViewScreenshots)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        
        // 스크린샷 파일들 로드
        loadScreenshotFiles()
        
        // 어댑터 설정
        adapter = ScreenshotAdapter(screenshotFiles) { file ->
            // 이미지 클릭 시 전체화면으로 보기 (나중에 구현)
            showFullScreenImage(file)
        }
        recyclerView.adapter = adapter
    }
    
    override fun onResume() {
        super.onResume()
        // 화면이 다시 보일 때마다 파일 목록 새로고침
        loadScreenshotFiles()
        adapter.updateFiles(screenshotFiles)
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
        android.widget.Toast.makeText(context, "이미지: ${file.name}", android.widget.Toast.LENGTH_SHORT).show()
    }
} 