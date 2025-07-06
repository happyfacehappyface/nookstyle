package com.example.nookstyle.util

import android.content.Context
import android.graphics.BitmapFactory
import com.example.nookstyle.model.ClothingPosition
import com.example.nookstyle.model.Villager
import org.json.JSONObject
import java.io.IOException

object VillagerLoader {
    
    /**
     * assets/images/villagers 폴더에서 모든 villager 정보를 동적으로 로드
     */
    fun loadVillagersFromAssets(context: Context): List<Villager> {
        val villagerList = mutableListOf<Villager>()
        val assetManager = context.assets
        
        try {
            // villagers 폴더 내의 모든 하위 폴더/파일 목록 가져오기
            val villagerFolders = assetManager.list("images/villagers")
            
            villagerFolders?.forEach { folderName ->
                try {
                    // 폴더 내의 파일들 확인
                    val folderPath = "images/villagers/$folderName"
                    val files = assetManager.list(folderPath)
                    
                    // PNG 파일과 info.json 파일 찾기
                    val pngFile = files?.find { it.endsWith(".png") }
                    val infoFile = files?.find { it == "info.json" }
                    
                    if (pngFile != null) {
                        val imagePath = "$folderPath/$pngFile"
                        
                        // info.json 파일이 있으면 읽어서 위치 정보와 이름 로드, 없으면 기본값 사용
                        val (clothingPositions, villagerName) = if (infoFile != null) {
                            loadVillagerInfoFromJson(assetManager, "$folderPath/$infoFile")
                        } else {
                            Pair(getDefaultClothingPositions(), folderName)
                        }
                        
                        val villager = Villager(
                            name = villagerName,
                            imagePath = imagePath,
                            hatPosition = clothingPositions.hatPosition,
                            topPosition = clothingPositions.topPosition,
                            bottomPosition = clothingPositions.bottomPosition,
                            shoesPosition = clothingPositions.shoesPosition
                        )
                        
                        villagerList.add(villager)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        return villagerList
    }
    
    /**
     * info.json 파일에서 villager 정보(이름과 ClothingPosition)를 로드
     */
    private fun loadVillagerInfoFromJson(assetManager: android.content.res.AssetManager, jsonPath: String): Pair<ClothingPositions, String> {
        return try {
            assetManager.open(jsonPath).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonString)
                
                val villagerName = jsonObject.optString("name", "Unknown")
                val clothingPositions = ClothingPositions(
                    hatPosition = parseClothingPosition(jsonObject, "hairPosition"), // info.json에서는 hairPosition으로 되어있음
                    topPosition = parseClothingPosition(jsonObject, "topPosition"),
                    bottomPosition = parseClothingPosition(jsonObject, "bottomPosition"),
                    shoesPosition = parseClothingPosition(jsonObject, "accessoryPosition") // info.json에서는 accessoryPosition으로 되어있음
                )
                
                Pair(clothingPositions, villagerName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(getDefaultClothingPositions(), "Unknown")
        }
    }
    
    /**
     * JSON 객체에서 ClothingPosition 파싱
     */
    private fun parseClothingPosition(jsonObject: JSONObject, key: String): ClothingPosition {
        return try {
            val positionObject = jsonObject.getJSONObject(key)
            ClothingPosition(
                x = positionObject.getDouble("x").toFloat(),
                y = positionObject.getDouble("y").toFloat(),
                scaleX = positionObject.optDouble("scaleX", 1.0).toFloat(),
                scaleY = positionObject.optDouble("scaleY", 1.0).toFloat(),
                rotation = positionObject.optDouble("rotation", 0.0).toFloat()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ClothingPosition(0.5f, 0.5f, 1.0f, 1.0f, 0.0f)
        }
    }
    
    /**
     * 기본 ClothingPosition 값들 반환
     */
    private fun getDefaultClothingPositions(): ClothingPositions {
        return ClothingPositions(
            hatPosition = ClothingPosition(0.5f, 0.22f, 2.0f, 1.5f, 0f),
            topPosition = ClothingPosition(0.50f, 0.61f, 1.24f, 0.78f, 0f),
            bottomPosition = ClothingPosition(0.5f, 0.75f, 0.9f, 0.41f, 0f),
            shoesPosition = ClothingPosition(0.48f, 0.82f, 0.62f, 0.42f, 0f)
        )
    }
    
    /**
     * ClothingPosition들을 그룹화하는 데이터 클래스
     */
    private data class ClothingPositions(
        val hatPosition: ClothingPosition,
        val topPosition: ClothingPosition,
        val bottomPosition: ClothingPosition,
        val shoesPosition: ClothingPosition
    )
} 