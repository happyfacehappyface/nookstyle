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
import com.example.nookstyle.model.ItemGroup


class ItemGroupAdapter(private var groupList: List<ItemGroup>)
    : RecyclerView.Adapter<ItemGroupAdapter.ItemGroupViewHolder>() {

    class ItemGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewItem)
        val titleText: TextView = view.findViewById(R.id.textViewTitle)
        val colorText: TextView = view.findViewById(R.id.textViewColor)
        val priceBellText: TextView = view.findViewById(R.id.textViewPrice_b)
        val priceMileText: TextView = view.findViewById(R.id.textViewPrice_m)
        val buttonPrev: ImageButton = view.findViewById(R.id.buttonPrev)
        val buttonNext: ImageButton = view.findViewById(R.id.buttonNext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemGroupViewHolder, position: Int) {
        val group = groupList[position]
        var currentIndex = 0
        val assetManager = holder.itemView.context.assets

        fun updateView(item: Item) {
            holder.titleText.text = item.title

            val allColors = group.items.joinToString(" / ") { it.color }
            holder.colorText.text = allColors

            holder.priceBellText.text = item.price_bell
            holder.priceMileText.text = item.price_mile
            loadImage(holder.imageView, item.imagePath, assetManager)
        }

        // 초기 이미지
        updateView(group.items[currentIndex])

        // <, > 버튼 표시 여부 결정
        if (group.items.size > 1) {
            holder.buttonPrev.visibility = View.VISIBLE
            holder.buttonNext.visibility = View.VISIBLE
        } else {
            holder.buttonPrev.visibility = View.GONE
            holder.buttonNext.visibility = View.GONE
        }

        // 다음 버튼 눌렀을 때
        holder.buttonNext.setOnClickListener {
            currentIndex = (currentIndex + 1) % group.items.size
            updateView(group.items[currentIndex])
        }

        // 이전 버튼 눌렀을 때
        holder.buttonPrev.setOnClickListener {
            currentIndex = (currentIndex - 1 + group.items.size) % group.items.size
            updateView(group.items[currentIndex])
        }
    }

    override fun getItemCount() = groupList.size

    /**
     * 데이터 업데이트
     */
    fun updateData(newGroupList: List<ItemGroup>) {
        groupList = newGroupList
        notifyDataSetChanged()
    }

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
