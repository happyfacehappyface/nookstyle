package com.example.nookstyle.ui.adapter

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val colorContainer: LinearLayout = view.findViewById(R.id.colorContainer)
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
        

        // 저장된 인덱스를 우선적으로 사용, 없으면 선택된 아이템 인덱스 사용, 둘 다 없으면 0
        val selectedItemIndex = getSelectedItemIndex(group)
        var currentIndex = currentIndexMap[group] ?: if (selectedItemIndex != -1) {
            selectedItemIndex
        } else {
            0
        }

        fun updateView(item: Item) {
            holder.titleText.text = group.title

            // 색상 사각형 생성 (현재 선택된 아이템 정보 전달)
            createColorSquares(holder.colorContainer, group.items, item)

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
    
    // 색상 사각형을 생성하는 함수
    private fun createColorSquares(colorContainer: LinearLayout, items: List<Item>, currentItem: Item) {
        colorContainer.removeAllViews()
        
        items.forEachIndexed { index, item ->
            val colorSquare = View(colorContainer.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(colorContainer.context, 16), // 16dp
                    dpToPx(colorContainer.context, 16)  // 16dp
                ).apply {
                    marginEnd = dpToPx(colorContainer.context, 4) // 4dp 간격
                }
                
                // 클릭 가능함을 나타내는 테두리 추가
                elevation = dpToPx(colorContainer.context, 2).toFloat()
                setPadding(dpToPx(colorContainer.context, 1), dpToPx(colorContainer.context, 1), 
                          dpToPx(colorContainer.context, 1), dpToPx(colorContainer.context, 1))
                
                // 현재 선택된 색상인지 확인하여 강조 표시
                val isCurrentColor = item.color == currentItem.color
                if (isCurrentColor) {
                    // 선택된 색상은 더 큰 크기와 테두리로 강조
                    layoutParams.width = dpToPx(colorContainer.context, 20)
                    layoutParams.height = dpToPx(colorContainer.context, 20)
                    elevation = dpToPx(colorContainer.context, 4).toFloat()
                    setPadding(dpToPx(colorContainer.context, 2), dpToPx(colorContainer.context, 2), 
                              dpToPx(colorContainer.context, 2), dpToPx(colorContainer.context, 2))
                    
                    // 선택된 색상에 검은색 테두리 추가
                    val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        setColor(getColorFromName(item.color))
                        setStroke(dpToPx(colorContainer.context, 2), android.graphics.Color.BLACK)
                        cornerRadius = dpToPx(colorContainer.context, 2).toFloat()
                    }
                    background = borderDrawable
                } else {
                    // 선택되지 않은 색상은 기본 스타일
                    val borderDrawable = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        setColor(getColorFromName(item.color))
                        setStroke(dpToPx(colorContainer.context, 1), android.graphics.Color.LTGRAY)
                        cornerRadius = dpToPx(colorContainer.context, 2).toFloat()
                    }
                    background = borderDrawable
                }
                
                // 색상 사각형 클릭 리스너 추가
                setOnClickListener {
                    // 클릭 효과 추가
                    alpha = 0.7f
                    postDelayed({ alpha = 1.0f }, 100)
                    
                    // 해당 색상의 아이템으로 바로 전환 (선택하지 않고 보기만)
                    val group = groupList.find { group ->
                        group.items.contains(item)
                    }
                    group?.let { foundGroup ->
                        // 현재 인덱스를 해당 색상의 인덱스로 업데이트
                        currentIndexMap[foundGroup] = index
                        // 어댑터 전체 업데이트
                        notifyDataSetChanged()
                    }
                }
            }
            colorContainer.addView(colorSquare)
        }
    }
    
    // dp를 px로 변환하는 함수
    private fun dpToPx(context: android.content.Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    
    // 색상 이름을 실제 색상 값으로 변환하는 함수
    private fun getColorFromName(colorName: String): Int {
        return when (colorName.lowercase()) {
            "빨강", "red" -> android.graphics.Color.RED
            "파랑", "blue" -> android.graphics.Color.BLUE
            "초록", "green" -> android.graphics.Color.GREEN
            "노랑", "yellow" -> android.graphics.Color.YELLOW
            "검정", "black" -> android.graphics.Color.BLACK
            "하양", "white" -> android.graphics.Color.WHITE
            "회색", "gray", "grey" -> android.graphics.Color.GRAY
            "주황", "orange" -> android.graphics.Color.rgb(255, 165, 0)
            "보라", "purple" -> android.graphics.Color.rgb(128, 0, 128)
            "분홍", "pink" -> android.graphics.Color.rgb(255, 192, 203)
            "갈색", "brown" -> android.graphics.Color.rgb(139, 69, 19)
            "하늘색", "skyblue" -> android.graphics.Color.rgb(135, 206, 235)
            "네이비", "navy" -> android.graphics.Color.rgb(0, 0, 128)
            "베이지", "beige" -> android.graphics.Color.rgb(245, 245, 220)
            "와인레드", "winered" -> android.graphics.Color.rgb(128, 0, 0)
            "코랄", "coral" -> android.graphics.Color.rgb(255, 127, 80)
            "아이보리", "ivory" -> android.graphics.Color.rgb(255, 255, 240)
            else -> android.graphics.Color.GRAY // 기본값
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
