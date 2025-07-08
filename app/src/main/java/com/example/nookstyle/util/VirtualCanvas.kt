package com.example.nookstyle.util

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.nookstyle.model.Item
import com.example.nookstyle.model.ItemGroup
import com.example.nookstyle.model.Villager
import com.example.nookstyle.model.ClothingPosition
import java.io.IOException

class VirtualCanvas(private val context: Context) {
    
    companion object {
        private const val CANVAS_WIDTH = 550
        private const val CANVAS_HEIGHT = 550
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
     * @param selectedHat 선택된 모자 아이템과 그룹
     * @param selectedTop 선택된 상의 아이템과 그룹
     * @param selectedBottom 선택된 하의 아이템과 그룹
     * @param selectedShoes 선택된 신발 아이템과 그룹
     * @return 렌더링된 비트맵
     */
    fun renderCharacter(
        villager: Villager?,
        selectedHat: Pair<Item?, ItemGroup?>?,
        selectedTop: Pair<Item?, ItemGroup?>?,
        selectedBottom: Pair<Item?, ItemGroup?>?,
        selectedShoes: Pair<Item?, ItemGroup?>?
    ): Bitmap {
        // 캔버스 초기화
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        
        try {
            // 1. 빌라저 기본 이미지 (가장 아래 레이어)
            villager?.let { v ->
                loadAndDrawImage(v.remainImagePath, 0, 0, v.width, v.height)
            }
            
            
            
            // 3. 기본 신발 (착용된 신발이 없을 때)
            if (selectedShoes?.first == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.shoesImagePath, 0, 0, v.width, v.height)
                }
            }
            
            // 4. 선택된 신발
            selectedShoes?.let { (shoes, group) ->
                if (shoes != null) {
                    val position = villager?.shoesPosition
                    val adjustedPosition = getAdjustedPosition(position, group)
                    loadAndDrawClothingImage(shoes.imagePath, adjustedPosition, villager?.width ?: 400, villager?.height ?: 400)
                }
            }
            
            // 5. 기본 하의 (착용된 하의가 없을 때)
            if (selectedBottom?.first == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.bottomImagePath, 0, 0, v.width, v.height)
                }
            }
            
            // 6. 선택된 하의
            selectedBottom?.let { (bottom, group) ->
                if (bottom != null) {
                    val position = villager?.bottomPosition
                    val adjustedPosition = getAdjustedPosition(position, group)
                    loadAndDrawClothingImage(bottom.imagePath, adjustedPosition, villager?.width ?: 400, villager?.height ?: 400)
                }
            }
            
            // 7. 기본 상의 (착용된 상의가 없을 때)
            if (selectedTop?.first == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.topImagePath, 0, 0, v.width, v.height)
                }
            }
            
            // 8. 선택된 상의
            selectedTop?.let { (top, group) ->
                if (top != null) {
                    val position = villager?.topPosition
                    val adjustedPosition = getAdjustedPosition(position, group)
                    loadAndDrawClothingImage(top.imagePath, adjustedPosition, villager?.width ?: 400, villager?.height ?: 400)
                }
            }

            // 2. 빌라저 헤드 이미지
            villager?.let { v ->
                loadAndDrawImage(v.headImagePath, 0, 0, v.width, v.height)
            }
            
            // 9. 기본 모자 (착용된 모자가 없을 때)
            if (selectedHat?.first == null) {
                villager?.let { v ->
                    loadAndDrawImage(v.hatImagePath, 0, 0, v.width, v.height)
                }
            }
            
            // 10. 선택된 모자 (가장 위 레이어)
            selectedHat?.let { (hat, group) ->
                if (hat != null) {
                    val position = villager?.hatPosition
                    val adjustedPosition = getAdjustedPosition(position, group)
                    loadAndDrawClothingImage(hat.imagePath, adjustedPosition, villager?.width ?: 400, villager?.height ?: 400)
                }
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
                
                // 캔버스 중앙에 위치하도록 계산
                val centerX = (CANVAS_WIDTH - width) / 2
                val centerY = (CANVAS_HEIGHT - height) / 2
                
                canvas.drawBitmap(scaledBitmap, centerX.toFloat(), centerY.toFloat(), paint)
                scaledBitmap.recycle()
                sourceBitmap.recycle()
            }
        } catch (e: IOException) {
            // 이미지가 존재하지 않으면 무시 (기본 의류가 없는 경우)
            Log.d(TAG, "Image not found: $imagePath")
        }
    }
    
    /**
     * 의류 이미지를 위치와 스케일에 맞춰 렌더링합니다.
     */
    private fun loadAndDrawClothingImage(imagePath: String?, position: ClothingPosition, baseWidth: Int, baseHeight: Int) {
        if (imagePath.isNullOrEmpty()) return
        
        try {
            val inputStream = context.assets.open(imagePath)
            val sourceBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (sourceBitmap != null) {
                // villager의 기본 위치와 ItemGroup의 조정값을 중복 적용
                val adjustedScaleX = position.scaleX
                val adjustedScaleY = position.scaleY
                
                val scaledWidth = (baseWidth * adjustedScaleX).toInt()
                val scaledHeight = (baseHeight * adjustedScaleY).toInt()
                
                // 위치 계산 (캔버스 전체 기준)
                val adjustedX = (CANVAS_WIDTH * position.x - scaledWidth / 2).toInt()
                val adjustedY = (CANVAS_HEIGHT * position.y - scaledHeight / 2).toInt()
                
                // 스케일된 비트맵 생성
                val scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, true)
                
                // 회전 적용
                if (position.rotation != 0f) {
                    val rotatedBitmap = rotateBitmap(scaledBitmap, position.rotation)
                    canvas.drawBitmap(rotatedBitmap, adjustedX.toFloat(), adjustedY.toFloat(), paint)
                    rotatedBitmap.recycle()
                } else {
                    canvas.drawBitmap(scaledBitmap, adjustedX.toFloat(), adjustedY.toFloat(), paint)
                }
                
                scaledBitmap.recycle()
                sourceBitmap.recycle()
            }
        } catch (e: IOException) {
            Log.d(TAG, "Image not found: $imagePath")
        }
    }
    
    /**
     * 비트맵을 회전시킵니다.
     */
    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    
    /**
     * villager의 기본 위치와 ItemGroup의 조정값을 결합합니다.
     */
    private fun getAdjustedPosition(villagerPosition: ClothingPosition?, itemGroup: ItemGroup?): ClothingPosition {
        if (villagerPosition == null) {
            return ClothingPosition(0.5f, 0.5f, 1.0f, 1.0f, 0.0f)
        }
        
        if (itemGroup == null) {
            return villagerPosition
        }
        
        // villager의 기본 위치와 ItemGroup의 조정값을 중복 적용
        val adjustedScaleX = villagerPosition.scaleX * itemGroup.scaleX
        val adjustedScaleY = villagerPosition.scaleY * itemGroup.scaleY
        val adjustedX = villagerPosition.x + itemGroup.x
        val adjustedY = villagerPosition.y + itemGroup.y
        val adjustedRotation = villagerPosition.rotation + itemGroup.rotation
        
        return ClothingPosition(
            x = adjustedX,
            y = adjustedY,
            scaleX = adjustedScaleX,
            scaleY = adjustedScaleY,
            rotation = adjustedRotation
        )
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