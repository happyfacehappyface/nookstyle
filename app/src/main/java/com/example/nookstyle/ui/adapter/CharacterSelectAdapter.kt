package com.example.nookstyle.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Villager
import java.io.IOException

class CharacterSelectAdapter(
    private val villagers: List<Villager>,
    private val onCharacterSelected: (Villager) -> Unit
) : RecyclerView.Adapter<CharacterSelectAdapter.CharacterViewHolder>() {

    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.characterThumbnail)
        val name: TextView = itemView.findViewById(R.id.characterName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character_select, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val villager = villagers[position]
        
        holder.name.text = villager.name
        
        // 썸네일 이미지 로드
        loadThumbnail(holder.thumbnail, villager)
        
        holder.itemView.setOnClickListener {
            onCharacterSelected(villager)
        }
    }

    override fun getItemCount(): Int = villagers.size

    private fun loadThumbnail(imageView: ImageView, villager: Villager) {
        try {
            // thumbnail.png 파일 경로 생성
            val folderPath = villager.imagePath.substringBeforeLast("/")
            val thumbnailPath = "$folderPath/thumbnail.png"
            
            val inputStream = imageView.context.assets.open(thumbnailPath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: IOException) {
            // thumbnail.png가 없으면 기본 이미지 사용
            try {
                val inputStream = imageView.context.assets.open(villager.imagePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
                inputStream.close()
            } catch (e2: IOException) {
                e2.printStackTrace()
                // 기본 아이콘 설정
                imageView.setImageResource(R.drawable.ic_person)
            }
        }
    }
} 