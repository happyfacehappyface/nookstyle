package com.example.nookstyle.ui.adapter

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Item


class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewItem)
        val titleText: TextView = view.findViewById(R.id.textViewTitle)
        val descText: TextView = view.findViewById(R.id.textViewDesc)
        val buttonPrev: ImageButton = view.findViewById(R.id.buttonPrev)
        val buttonNext: ImageButton = view.findViewById(R.id.buttonNext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        val context = holder.itemView.context
        val assetManager: AssetManager = context.assets

        holder.titleText.text = item.title
        holder.descText.text = item.description

        // folderPath 계산: imagePath에서 마지막 '/' 전까지 자름
        // 예) images/cloths/top/TopsTexTopTshirtsHNumberball2.webp
        //  -> images/cloths/top
        val lastSlash = item.imagePath.lastIndexOf('/')
        val folderPath = if (lastSlash > 0) item.imagePath.substring(0, lastSlash) else ""

        // 해당 폴더에서 파일 리스트 읽기
        val imageFiles = assetManager.list(folderPath)?.sortedArray() ?: arrayOf()
        var currentIndex = 0


        // 처음엔 imagePath (고정) 보여주기
        loadImage(holder.imageView, item.imagePath, assetManager)

        // 다음 버튼 눌렀을 때
        holder.buttonNext.setOnClickListener {
            if (imageFiles.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % imageFiles.size
                loadImage(holder.imageView, "$folderPath/${imageFiles[currentIndex]}", assetManager)
            }
        }

        // 이전 버튼 눌렀을 때
        holder.buttonPrev.setOnClickListener {
            if (imageFiles.isNotEmpty()) {
                currentIndex = (currentIndex - 1 + imageFiles.size) % imageFiles.size
                loadImage(holder.imageView, "$folderPath/${imageFiles[currentIndex]}", assetManager)
            }
        }
    }

    override fun getItemCount() = itemList.size

    // assets에서 이미지를 불러와 ImageView에 넣는 함수
    private fun loadImage(imageView: ImageView, assetPath: String, assetManager: AssetManager) {
        try {
            assetManager.open(assetPath).use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
