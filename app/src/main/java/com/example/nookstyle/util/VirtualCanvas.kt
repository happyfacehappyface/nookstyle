package com.example.nookstyle.util

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.nookstyle.model.Item
import com.example.nookstyle.model.Villager
import java.io.IOException

class VirtualCanvas(private val context: Context) {
    
    companion object {
        private const val CANVAS_WIDTH = 512
        private const val CANVAS_HEIGHT = 512
        private const val TAG = "VirtualCanvas"
    }
    
    private val canvas = Canvas()
    private val bitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888)
    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }
    
    init {
        canvas.setBitmap(bitmap)
    }
    
    /**
     * 가상 캔버스에 모든 이미지를 렌더링합니다.
     * @param villager 선택된 빌라저
     * @param selectedHat 선택된 모자 아이템
     * @param selectedTop 선택된 상의 아이템
     * @param selectedBottom 선택된 하의 아이템
     * @param selectedShoes 선택된 신발 아이템
     * @return 렌더링된 비트맵
     */
    fun renderCharacter(
        villager: Villager?,
        selectedHat: Item?,
        selectedTop: Item?,
        selectedBottom: Item?,
        selectedShoes: Item?
    ): Bitmap {
        // 캔버스 초기화
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        
        try {
            // 1. 빌라저 기본 이미지 (가장 아래 레이어)
            villager?.let { v ->
                loadAndDrawImage(v.remainImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
            // 2. 빌라저 헤드 이미지
            villager?.let { v ->
                loadAndDrawImage(v.headImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
            // 3. 기본 신발 (착용된 신발이 없을 때)
            if (selectedShoes == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.shoesImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
                }
            }
            
            // 4. 선택된 신발
            selectedShoes?.let { shoes ->
                loadAndDrawImage(shoes.imagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
            // 5. 기본 하의 (착용된 하의가 없을 때)
            if (selectedBottom == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.bottomImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
                }
            }
            
            // 6. 선택된 하의
            selectedBottom?.let { bottom ->
                loadAndDrawImage(bottom.imagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
            // 7. 기본 상의 (착용된 상의가 없을 때)
            if (selectedTop == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.topImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
                }
            }
            
            // 8. 선택된 상의
            selectedTop?.let { top ->
                loadAndDrawImage(top.imagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
            // 9. 기본 모자 (착용된 모자가 없을 때)
            if (selectedHat == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.hatImagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
                }
            }
            
            // 10. 선택된 모자 (가장 위 레이어)
            selectedHat?.let { hat ->
                loadAndDrawImage(hat.imagePath, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error rendering character: ${e.message}")
        }
        
        return bitmap
    }
    
    /**
     * 이미지를 로드하고 캔버스에 그립니다.
     */
    private fun loadAndDrawImage(imagePath: String?, x: Int, y: Int, width: Int, height: Int) {
        if (imagePath.isNullOrEmpty()) return
        
        try {
            val inputStream = context.assets.open(imagePath)
            val sourceBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (sourceBitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, width, height, true)
                canvas.drawBitmap(scaledBitmap, x.toFloat(), y.toFloat(), paint)
                scaledBitmap.recycle()
                sourceBitmap.recycle()
            }
        } catch (e: IOException) {
            // 이미지가 존재하지 않으면 무시 (기본 의류가 없는 경우)
            Log.d(TAG, "Image not found: $imagePath")
        }
    }
    
    /**
     * 캔버스의 현재 상태를 비트맵으로 반환합니다.
     */
    fun getCurrentBitmap(): Bitmap {
        return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
    }
    
    /**
     * 리소스를 정리합니다.
     */
    fun cleanup() {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }
} 