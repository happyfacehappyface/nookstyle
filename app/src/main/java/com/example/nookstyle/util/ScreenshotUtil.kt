package com.example.nookstyle.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ScreenshotUtil {
    
    companion object {
        
        /**
         * 특정 View의 스크린샷을 찍어서 Bitmap으로 반환
         */
        fun captureView(view: View): Bitmap? {
            return try {
                // View가 그려질 때까지 기다림
                view.post {
                    view.invalidate()
                }
                
                // View의 크기 확인
                if (view.width <= 0 || view.height <= 0) {
                    return null
                }
                
                // Bitmap 생성
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                
                // View를 Canvas에 그리기
                view.draw(canvas)
                
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        /**
         * Bitmap을 파일로 저장
         */
        fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String? = null): String? {
            return try {
                // 파일명 생성 (현재 시간 기준)
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = fileName ?: "Screenshot_$timeStamp"
                
                // 저장할 디렉토리 생성
                val picturesDir = File(context.getExternalFilesDir(null), "Screenshots")
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs()
                }
                
                // 파일 생성 (확장자 png로 변경)
                val imageFile = File(picturesDir, "$imageFileName.png")
                val outputStream = FileOutputStream(imageFile)
                
                // Bitmap을 PNG로 압축하여 저장 (투명도 지원)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                
                // 저장 완료 메시지
                Toast.makeText(context, "스크린샷이 저장되었습니다: ${imageFile.absolutePath}", Toast.LENGTH_LONG).show()
                
                imageFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "스크린샷 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                null
            }
        }
        
        /**
         * View의 스크린샷을 찍어서 바로 파일로 저장
         */
        fun captureAndSaveView(context: Context, view: View, fileName: String? = null): String? {
            val bitmap = captureView(view)
            return bitmap?.let { saveBitmapToFile(context, it, fileName) }
        }
        
        /**
         * 특정 영역의 스크린샷을 찍어서 저장 (좌표 기반)
         */
        fun captureAreaAndSave(
            context: Context, 
            view: View, 
            left: Int, 
            top: Int, 
            right: Int, 
            bottom: Int, 
            fileName: String? = null
        ): String? {
            return try {
                val fullBitmap = captureView(view)
                if (fullBitmap != null) {
                    // 지정된 영역만 잘라내기
                    val croppedBitmap = Bitmap.createBitmap(fullBitmap, left, top, right - left, bottom - top)
                    saveBitmapToFile(context, croppedBitmap, fileName)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "영역 스크린샷에 실패했습니다.", Toast.LENGTH_SHORT).show()
                null
            }
        }
    }
} 