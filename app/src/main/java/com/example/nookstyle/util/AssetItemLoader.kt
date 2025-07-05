package com.example.nookstyle.util

import android.content.Context
import android.util.Log
import com.example.nookstyle.model.Item
import com.example.nookstyle.model.ItemGroup
import com.example.nookstyle.model.ItemTag
import org.json.JSONObject
import java.io.IOException

object AssetItemLoader {
    
    private const val TAG = "AssetItemLoader"
    private const val CLOTHS_PATH = "images/cloths"
    
    fun loadItemsFromAssets(context: Context): List<ItemGroup> {
        val itemGroups = mutableListOf<ItemGroup>()
        
        try {
            // 각 카테고리 폴더 탐색
            val categories = context.assets.list(CLOTHS_PATH) ?: return emptyList()
            
            for (category in categories) {
                val categoryPath = "$CLOTHS_PATH/$category"
                val itemTag = getItemTagFromCategory(category)
                
                // 카테고리 내의 아이템 폴더들 탐색
                val itemFolders = context.assets.list(categoryPath) ?: continue
                
                for (itemFolder in itemFolders) {
                    val itemFolderPath = "$categoryPath/$itemFolder"
                    
                    try {
                        val itemGroup = createItemGroupFromFolder(context, itemFolderPath, itemTag)
                        if (itemGroup != null) {
                            itemGroups.add(itemGroup)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading item from folder: $itemFolderPath", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading items from assets", e)
        }
        
        return itemGroups
    }
    
    private fun getItemTagFromCategory(category: String): ItemTag {
        return when (category.lowercase()) {
            "hat" -> ItemTag.HAT
            "top" -> ItemTag.TOP
            "bottom" -> ItemTag.BOTTOM
            "shoes" -> ItemTag.SHOES
            else -> ItemTag.TOP // 기본값
        }
    }
    
    private fun createItemGroupFromFolder(context: Context, folderPath: String, itemTag: ItemTag): ItemGroup? {
        try {
            // info.json 파일 읽기
            val infoJson = readInfoJson(context, folderPath)
            if (infoJson == null) {
                Log.w(TAG, "No info.json found in: $folderPath")
                return null
            }
            
            val title = infoJson.optString("title", "Unknown")
            val priceBell = infoJson.optString("price_bell", "0")
            val priceMile = infoJson.optString("price_mile", "0")
            
            // 이미지 파일들 찾기
            val imageFiles = context.assets.list(folderPath)?.filter { 
                it.lowercase().endsWith(".webp") || it.lowercase().endsWith(".png") || it.lowercase().endsWith(".jpg")
            } ?: return null
            
            if (imageFiles.isEmpty()) {
                Log.w(TAG, "No image files found in: $folderPath")
                return null
            }
            
            // 아이템들 생성
            val items = imageFiles.map { fileName ->
                val color = extractColorFromFileName(fileName)
                val imagePath = "$folderPath/$fileName"
                Item(color = color, imagePath = imagePath)
            }
            
            return ItemGroup(
                title = title,
                tag = itemTag,
                price_bell = priceBell,
                price_mile = priceMile,
                items = items
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating item group from folder: $folderPath", e)
            return null
        }
    }
    
    private fun readInfoJson(context: Context, folderPath: String): JSONObject? {
        return try {
            val infoPath = "$folderPath/info.json"
            context.assets.open(infoPath).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                JSONObject(jsonString)
            }
        } catch (e: IOException) {
            null
        }
    }
    
    private fun extractColorFromFileName(fileName: String): String {
        // 파일명에서 색상 추출 (예: "1_black.webp" -> "검정")
        val nameWithoutExtension = fileName.substringBeforeLast(".")
        val parts = nameWithoutExtension.split("_")
        
        val englishColor = if (parts.size >= 2) {
            parts[1] // 두 번째 부분이 색상
        } else {
            "unknown"
        }
        
        return translateColorToKorean(englishColor)
    }
    
    private fun translateColorToKorean(englishColor: String): String {
        return when (englishColor.lowercase()) {
            "red" -> "빨강"
            "black" -> "검정"
            "blue" -> "파랑"
            "green" -> "초록"
            "yellow" -> "노랑"
            "white" -> "하양"
            "brown" -> "갈색"
            "purple" -> "보라"
            "orange" -> "주황"
            "pink" -> "분홍"
            "gray", "grey" -> "회색"
            else -> englishColor // 번역할 수 없는 색상은 그대로 반환
        }
    }
} 