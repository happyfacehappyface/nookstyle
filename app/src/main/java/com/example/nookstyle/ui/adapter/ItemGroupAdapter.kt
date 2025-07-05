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
import com.example.nookstyle.util.SelectedItemsManager


class ItemGroupAdapter(
    private var groupList: List<ItemGroup>,
    private val onItemSelected: ((Item, ItemGroup) -> Unit)? = null
) : RecyclerView.Adapter<ItemGroupAdapter.ItemGroupViewHolder>() {
    
    // 각 ItemGroup별로 현재 보고 있는 아이템의 인덱스를 저장
    private val currentIndexMap = mutableMapOf<ItemGroup, Int>()

    class ItemGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewItem)
        val titleText: TextView = view.findViewById(R.id.textViewTitle)
        val colorText: TextView = view.findViewById(R.id.textViewColor)
        val priceBellText: TextView = view.findViewById(R.id.textViewPrice_b)
        val priceMileText: TextView = view.findViewById(R.id.textViewPrice_m)
        val buttonPrev: ImageButton = view.findViewById(R.id.buttonPrev)
        val buttonNext: ImageButton = view.findViewById(R.id.buttonNext)
        val itemContainer: View = view.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemGroupViewHolder, position: Int) {
        val group = groupList[position]
        val assetManager = holder.itemView.context.assets
        

        // 선택된 아이템이 있으면 해당 인덱스 사용, 없으면 저장된 인덱스 사용, 둘 다 없으면 0
        val selectedItemIndex = getSelectedItemIndex(group)
        var currentIndex = if (selectedItemIndex != -1) {
            selectedItemIndex
        } else {
            currentIndexMap[group] ?: 0
        }

        fun updateView(item: Item) {
            holder.titleText.text = group.title

            val allColors = group.items.joinToString(" / ") { it.color }
            holder.colorText.text = allColors

            holder.priceBellText.text = "${group.price_bell} 벨"
            holder.priceMileText.text = "${group.price_mile} 마일"
            loadImage(holder.imageView, item.imagePath, assetManager)
            
            // 선택된 아이템인지 확인하고 배경색 변경
            updateItemSelectionBackground(holder, item, group)
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
            currentIndexMap[group] = currentIndex
            updateView(group.items[currentIndex])
        }

        // 이전 버튼 눌렀을 때
        holder.buttonPrev.setOnClickListener {
            currentIndex = (currentIndex - 1 + group.items.size) % group.items.size
            currentIndexMap[group] = currentIndex
            updateView(group.items[currentIndex])
        }

        // 아이템 클릭 시 선택 리스너 호출
        holder.itemContainer.setOnClickListener {
            onItemSelected?.invoke(group.items[currentIndex], group)
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
    
    /**
     * 특정 ItemGroup의 현재 인덱스 업데이트
     */
    fun updateCurrentIndex(group: ItemGroup, index: Int) {
        currentIndexMap[group] = index
    }
    
    // 선택된 아이템의 인덱스를 찾는 함수
    private fun getSelectedItemIndex(group: ItemGroup): Int {
        val selectedItem = when (group.tag) {
            com.example.nookstyle.model.ItemTag.HAT -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedHat()
                if (selectedGroup == group) selectedItem else null
            }
            com.example.nookstyle.model.ItemTag.TOP -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedTop()
                if (selectedGroup == group) selectedItem else null
            }
            com.example.nookstyle.model.ItemTag.BOTTOM -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedBottom()
                if (selectedGroup == group) selectedItem else null
            }
            com.example.nookstyle.model.ItemTag.SHOES -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedShoes()
                if (selectedGroup == group) selectedItem else null
            }
        }
        
        return selectedItem?.let { item ->
            group.items.indexOfFirst { it.color == item.color && it.imagePath == item.imagePath }
        } ?: -1
    }

    // 선택된 아이템의 배경색 업데이트
    private fun updateItemSelectionBackground(holder: ItemGroupViewHolder, item: Item, group: ItemGroup) {
        val isSelected = when (group.tag) {
            com.example.nookstyle.model.ItemTag.HAT -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedHat()
                selectedItem == item && selectedGroup == group
            }
            com.example.nookstyle.model.ItemTag.TOP -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedTop()
                selectedItem == item && selectedGroup == group
            }
            com.example.nookstyle.model.ItemTag.BOTTOM -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedBottom()
                selectedItem == item && selectedGroup == group
            }
            com.example.nookstyle.model.ItemTag.SHOES -> {
                val (selectedItem, selectedGroup) = SelectedItemsManager.getSelectedShoes()
                selectedItem == item && selectedGroup == group
            }
        }
        
        if (isSelected) {
            holder.itemContainer.setBackgroundResource(R.drawable.selected_item_background)
        } else {
            holder.itemContainer.setBackgroundResource(android.R.color.white)
        }
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
