package com.example.nookstyle.util

import java.io.File
import kotlin.random.Random

object LikeCountManager {
    private val likeCounts = mutableMapOf<String, Int>()
    private val likeStates = mutableMapOf<String, Boolean>() // 좋아요 상태 저장
    private var isInitialized = false
    
    /**
     * 좋아요 수 초기화 (앱 시작 시 한 번만 호출)
     */
    fun initializeLikeCounts(context: android.content.Context) {
        if (isInitialized) return
        
        try {
            // 1. assets/contest 폴더의 이미지들에 좋아요 수 할당
            val assetManager = context.assets
            val assetFiles = assetManager.list("contest")
            
            assetFiles?.filter { fileName ->
                fileName.lowercase().endsWith(".jpg") || 
                fileName.lowercase().endsWith(".jpeg") || 
                fileName.lowercase().endsWith(".png") ||
                fileName.lowercase().endsWith(".webp")
            }?.forEach { fileName ->
                val imageKey = "assets/$fileName"
                likeCounts[imageKey] = Random.nextInt(0, 100)
            }
            
            // 2. 외부 저장소 contest 폴더의 이미지들에 좋아요 수 할당
            val contestDir = File(context.getExternalFilesDir(null), "contest")
            if (contestDir.exists() && contestDir.isDirectory) {
                val externalFiles = contestDir.listFiles { file ->
                    file.isFile && (file.extension.lowercase() == "jpg" || 
                                   file.extension.lowercase() == "jpeg" || 
                                   file.extension.lowercase() == "png" ||
                                   file.extension.lowercase() == "webp")
                }
                
                externalFiles?.forEach { file ->
                    val imageKey = "external/${file.name}"
                    likeCounts[imageKey] = Random.nextInt(0, 100)
                }
            }
            
            isInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 이미지의 좋아요 수 가져오기
     */
    fun getLikeCount(imageName: String): Int {
        return likeCounts[imageName] ?: 0
    }
    
    /**
     * 이미지의 좋아요 수 설정하기
     */
    fun setLikeCount(imageName: String, count: Int) {
        likeCounts[imageName] = count
    }
    
    /**
     * 새로운 이미지 추가 시 좋아요 수 할당
     */
    fun addNewImage(imageName: String) {
        if (!likeCounts.containsKey(imageName)) {
            likeCounts[imageName] = Random.nextInt(0, 100)
        }
    }
    
    /**
     * 이미지 삭제 시 좋아요 수 제거
     */
    fun removeImage(imageName: String) {
        likeCounts.remove(imageName)
        likeStates.remove(imageName)
    }
    
    /**
     * 이미지의 좋아요 상태 가져오기
     */
    fun getLikeState(imageName: String): Boolean {
        return likeStates[imageName] ?: false
    }
    
    /**
     * 이미지의 좋아요 상태 설정하기
     */
    fun setLikeState(imageName: String, isLiked: Boolean) {
        likeStates[imageName] = isLiked
    }
    
    /**
     * 좋아요 토글 (상태 변경 + 카운트 조정)
     */
    fun toggleLike(imageName: String): Int {
        val currentState = getLikeState(imageName)
        val newState = !currentState
        setLikeState(imageName, newState)
        
        val currentCount = getLikeCount(imageName)
        val newCount = if (newState) currentCount + 1 else currentCount - 1
        setLikeCount(imageName, newCount)
        
        return newCount
    }
} 