package com.example.nookstyle.ui.fragments

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.*
import com.example.nookstyle.model.ColorItem
import com.example.nookstyle.ui.adapter.CharacterSelectAdapter
import com.example.nookstyle.ui.adapter.ColorFilterAdapter
import com.example.nookstyle.ui.adapter.ItemGroupAdapter
import com.example.nookstyle.util.AssetItemLoader
import com.example.nookstyle.util.SelectedItemsManager
import com.example.nookstyle.util.ScreenshotUtil
import com.example.nookstyle.util.VillagerLoader
import com.example.nookstyle.util.SelectedCharacterManager
import com.example.nookstyle.util.VirtualCanvas
import java.io.IOException



class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemGroupAdapter

    // 가상 캔버스로 렌더링된 결과를 표시할 ImageView
    private lateinit var virtualCanvasImageView: ImageView
    
    // 가상 캔버스 인스턴스
    private lateinit var virtualCanvas: VirtualCanvas

    // 캐릭터 선택 버튼
    private lateinit var chooseCharacter: ImageButton

    

    
    // 착용 중인 아이템 표시 이미지들
    private lateinit var equippedHatImage: ImageView
    private lateinit var equippedTopImage: ImageView
    private lateinit var equippedBottomImage: ImageView
    private lateinit var equippedShoesImage: ImageView
    
    // 태그 버튼들
    private lateinit var btnAll: Button
    private lateinit var btnTop: Button
    private lateinit var btnBottom: Button
    private lateinit var btnHat: Button
    private lateinit var btnShoes: Button

    // 저장 버튼
    private lateinit var btnSave: Button
    
    // 검색창
    private lateinit var searchEditText: EditText
    
    // 색상 필터 버튼
    private lateinit var colorButton: ImageButton
    
    // 가격 필터 버튼
    private lateinit var priceButton: ImageButton
    
    // 필터링 상태
    private var currentFilter: ItemTag? = null
    private var currentSearchQuery: String = ""
    private var currentColorFilter: String? = null
    private var currentPriceFilter: PriceFilter? = null


    // 전체 빌라저 리스트
    val villagerList = mutableListOf<Villager>()
    
    // 캐릭터 선택 다이얼로그
    private var dialog: AlertDialog? = null






    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerView)
        virtualCanvasImageView = view.findViewById(R.id.virtualCanvasImageView)
        
        // 가상 캔버스 초기화
        virtualCanvas = VirtualCanvas(requireContext())

        // 캐릭터 버튼 초기화
        chooseCharacter = view.findViewById(R.id.chooseCharacter)
        chooseCharacter.setOnClickListener {
            showCharacterSelectDialog()
        }

        // 착용 중인 아이템 표시 이미지들 초기화
        equippedHatImage = view.findViewById<ImageView?>(R.id.equippedHatImage).apply { setImageResource(R.drawable.ic_add) }
        equippedTopImage = view.findViewById<ImageView?>(R.id.equippedTopImage).apply { setImageResource(R.drawable.ic_add) }
        equippedBottomImage = view.findViewById<ImageView?>(R.id.equippedBottomImage).apply { setImageResource(R.drawable.ic_add) }
        equippedShoesImage = view.findViewById<ImageView?>(R.id.equippedShoesImage).apply { setImageResource(R.drawable.ic_add) }

        // 착용 아이템 클릭 리스너 설정
        equippedHatImage.setOnClickListener {
            val (item, _) = SelectedItemsManager.getSelectedHat()
            if (item == null) {
                filterItems(ItemTag.HAT)
            } else {
                unequipItem(ItemTag.HAT)
            }
        }
        equippedTopImage.setOnClickListener {
            val (item, _) = SelectedItemsManager.getSelectedTop()
            if (item == null) {
                filterItems(ItemTag.TOP)
            } else {
                unequipItem(ItemTag.TOP)
            }
        }
        equippedBottomImage.setOnClickListener {
            val (item, _) = SelectedItemsManager.getSelectedBottom()
            if (item == null) {
                filterItems(ItemTag.BOTTOM)
            } else {
                unequipItem(ItemTag.BOTTOM)
            }
        }
        equippedShoesImage.setOnClickListener {
            val (item, _) = SelectedItemsManager.getSelectedShoes()
            if (item == null) {
                filterItems(ItemTag.SHOES)
            } else {
                unequipItem(ItemTag.SHOES)
            }
        }
        
        // 태그 버튼 초기화
        btnAll = view.findViewById(R.id.btnAll)
        btnTop = view.findViewById(R.id.btnTop)
        btnBottom = view.findViewById(R.id.btnBottom)
        btnHat = view.findViewById(R.id.btnHat)
        btnShoes = view.findViewById(R.id.btnShoes)
        
        // 저장 버튼 초기화
        btnSave = view.findViewById(R.id.btnSave)
        
        // 검색창 초기화
        searchEditText = view.findViewById(R.id.searchEditText)
        
        // 색상 필터 버튼 초기화
        colorButton = view.findViewById(R.id.colorButton)
        colorButton.setOnClickListener {
            showColorFilterDialog()
        }
        
        // 가격 필터 버튼 초기화
        priceButton = view.findViewById(R.id.priceButton)
        priceButton.setOnClickListener {
            showPriceFilterDialog()
        }
        

        
        // RecyclerView 설정
        recyclerView.layoutManager = GridLayoutManager(context, 2)



        

        setupData()
    }

    // ItemGroup 데이터 세팅
    private fun setupData() {
        if(globalItemGroups.isEmpty()) {
            // assets 폴더에서 자동으로 아이템 로드
            val loadedItemGroups = AssetItemLoader.loadItemsFromAssets(requireContext())
            globalItemGroups.addAll(loadedItemGroups)
        }

        adapter = ItemGroupAdapter(globalItemGroups) { selectedItem, selectedGroup ->
            onItemSelected(selectedItem, selectedGroup)
        }
        recyclerView.adapter = adapter
        
        // 태그 버튼 클릭 리스너 설정
        setupTagButtons()
        
        // 검색 기능 설정
        setupSearchFunction()
        
        // 저장 버튼 클릭 리스너 설정
        setupSaveButton()
        
        // 빌라저 초기화
        setupVillager()
        
        // 가상 캔버스로 캐릭터 렌더링
        renderCharacterOnVirtualCanvas()
    }

    // 캐릭터 선택 버튼 설정
    private fun showCharacterSelectDialog() {
        // 커스텀 다이얼로그 레이아웃 인플레이트
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_character_select, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.characterRecyclerView)
        
        // RecyclerView 설정 - 그리드 레이아웃으로 변경 (3열)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
        
        // 어댑터 설정
        val adapter = CharacterSelectAdapter(villagerList) { selectedVillager ->
            SelectedCharacterManager.setSelectedVillager(selectedVillager)
            
            // 가상 캔버스로 캐릭터 렌더링
            renderCharacterOnVirtualCanvas()
            
            Toast.makeText(context, "${selectedVillager.name} 캐릭터로 변경되었습니다.", Toast.LENGTH_SHORT).show()
            
            // 다이얼로그 닫기
            dialog?.dismiss()
        }
        
        recyclerView.adapter = adapter
        
        // 다이얼로그 생성 및 표시
        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("취소", null)
            .create()
        
        dialog?.show()
    }


    // 태그 버튼 설정
    private fun setupTagButtons() {
        btnAll.setOnClickListener { filterItems(null) }
        btnTop.setOnClickListener { filterItems(ItemTag.TOP) }
        btnBottom.setOnClickListener { filterItems(ItemTag.BOTTOM) }
        btnHat.setOnClickListener { filterItems(ItemTag.HAT) }
        btnShoes.setOnClickListener { filterItems(ItemTag.SHOES) }
    }
    
    // 저장 버튼 설정
    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            captureAndSaveScreenshot()
        }
    }
    
    // 스크린샷 캡처 및 저장
    private fun captureAndSaveScreenshot() {
        showFileNameInputDialog()
    }
    
    // 파일명 입력 다이얼로그 표시
    private fun showFileNameInputDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filename_input, null)
        val editTextFileName = dialogView.findViewById<EditText>(R.id.editTextFileName)
        
        // 기본 파일명을 빈 문자열로 설정
        editTextFileName.setText("")
        
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("스크린샷 저장")
            .setMessage("저장할 파일명을 입력하세요")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                val fileName = editTextFileName.text.toString().trim()
                if (fileName.isNotEmpty()) {
                    captureAndSaveWithFileName(fileName)
                } else {
                    Toast.makeText(context, "파일명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .create()
        
        dialog.show()
    }
    
    // 파일명으로 스크린샷 캡처 및 저장
    private fun captureAndSaveWithFileName(fileName: String) {
        try {
            // 가상 캔버스의 현재 비트맵을 직접 저장
            val currentBitmap = virtualCanvas.getCurrentBitmap()
            val savedPath = ScreenshotUtil.saveBitmapToFile(requireContext(), currentBitmap, fileName)
            if (savedPath != null) {
                Toast.makeText(context, "스크린샷이 저장되었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "스크린샷 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "스크린샷 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 색상 필터 다이얼로그 표시
    private fun showColorFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_color_filter, null)
        val colorRecyclerView = dialogView.findViewById<RecyclerView>(R.id.colorRecyclerView)
        val btnClearFilter = dialogView.findViewById<Button>(R.id.btnClearFilter)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        
        // 색상 목록 생성
        val colorList = createColorList()
        
        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        // RecyclerView 설정 - 팝업 중앙 정렬을 위한 GridLayoutManager
        val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 4)
        colorRecyclerView.layoutManager = gridLayoutManager
        
        // 아이템 데코레이션 추가로 팝업 중앙 정렬 개선
        colorRecyclerView.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: androidx.recyclerview.widget.RecyclerView,
                state: androidx.recyclerview.widget.RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val spanCount = 4
                val column = position % spanCount
                
                // 팝업 중앙 정렬을 위한 여백 조정
                val spacing = 12
                outRect.left = if (column == 0) spacing else spacing / 2
                outRect.right = if (column == spanCount - 1) spacing else spacing / 2
                outRect.top = spacing
                outRect.bottom = spacing
            }
        })
        
        val colorAdapter = ColorFilterAdapter(colorList) { selectedColor ->
            if (selectedColor.isClearFilter) {
                // 필터 해제 버튼 클릭 시
                currentColorFilter = null
                applyFilters()
                dialog.dismiss()
            } else {
                // 일반 색상 선택 시
                currentColorFilter = selectedColor.colorName
                applyFilters()
                dialog.dismiss() // 색상 선택 시 다이얼로그 닫기
            }
        }
        
        // 현재 선택된 색상이 있다면 어댑터에 설정
        if (currentColorFilter != null) {
            val currentColorItem = colorList.find { it.colorName == currentColorFilter }
            currentColorItem?.let { colorAdapter.setSelectedColor(it) }
        }
        
        colorRecyclerView.adapter = colorAdapter
        
        // 버튼 리스너 설정
        btnClearFilter.setOnClickListener {
            currentColorFilter = null
            colorAdapter.clearSelection()
            applyFilters()
            dialog.dismiss()
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        // 닫기 버튼 리스너 설정
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    // 가격 필터 다이얼로그 표시
    private fun showPriceFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_price_filter, null)
        val btnBells = dialogView.findViewById<ImageButton>(R.id.btnBells)
        val btnMiles = dialogView.findViewById<ImageButton>(R.id.btnMiles)
        val priceRangeSlider = dialogView.findViewById<com.google.android.material.slider.RangeSlider>(R.id.priceRangeSlider)
        val priceRangeText = dialogView.findViewById<android.widget.TextView>(R.id.priceRangeText)
        val btnClearFilter = dialogView.findViewById<Button>(R.id.btnClearFilter)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<ImageButton>(R.id.btnCancel)
        
        // RangeSlider 초기값 설정
        if (currentPriceFilter != null) {
            // 현재 가격 필터가 있다면 초기값 설정
            when (currentPriceFilter!!.currencyType) {
                CurrencyType.BELLS -> {
                    btnBells.setBackgroundResource(R.drawable.rounded_button_selected)
                    btnMiles.setBackgroundResource(R.drawable.rounded_button_background)
                    priceRangeSlider.valueTo = 5000f
                    priceRangeSlider.setValues(currentPriceFilter!!.minPrice.toFloat(), currentPriceFilter!!.maxPrice.toFloat())
                    priceRangeText.text = "가격 범위: ${currentPriceFilter!!.minPrice} - ${currentPriceFilter!!.maxPrice}"
                }
                CurrencyType.MILES -> {
                    btnMiles.setBackgroundResource(R.drawable.rounded_button_selected)
                    btnBells.setBackgroundResource(R.drawable.rounded_button_background)
                    priceRangeSlider.valueTo = 1000f
                    priceRangeSlider.setValues(currentPriceFilter!!.minPrice.toFloat(), currentPriceFilter!!.maxPrice.toFloat())
                    priceRangeText.text = "가격 범위: ${currentPriceFilter!!.minPrice} - ${currentPriceFilter!!.maxPrice}"
                }
            }
        } else {
            // 기본값 설정 (벨이 기본 선택)
            btnBells.setBackgroundResource(R.drawable.rounded_button_selected)
            btnMiles.setBackgroundResource(R.drawable.rounded_button_background)
            priceRangeSlider.valueTo = 5000f
            priceRangeSlider.setValues(0f, 5000f)
            priceRangeText.text = "가격 범위: 0 - 5000"
        }
        
        // RangeSlider 리스너 설정
        priceRangeSlider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser && slider.values.size >= 2) {
                val minValue = slider.values[0].toInt()
                val maxValue = slider.values[1].toInt()
                priceRangeText.text = "가격 범위: $minValue - $maxValue"
                
                // 현재 선택된 통화 타입 확인
                val currencyType = when {
                    btnBells.background.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_selected)?.constantState -> CurrencyType.BELLS
                    btnMiles.background.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button_selected)?.constantState -> CurrencyType.MILES
                    else -> CurrencyType.BELLS
                }
                
                currentPriceFilter = PriceFilter(
                    currencyType = currencyType,
                    minPrice = minValue,
                    maxPrice = maxValue
                )
                applyFilters()
            }
        }
        
        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        
        // 버튼 리스너 설정
        btnClearFilter.setOnClickListener {
            currentPriceFilter = null
            applyFilters()
            dialog.dismiss()
        }
        
        btnConfirm.setOnClickListener {
            dialog.dismiss()
        }
        
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        // 통화 선택 버튼 클릭 리스너
        btnBells.setOnClickListener {
            btnBells.setBackgroundResource(R.drawable.rounded_button_selected)
            btnMiles.setBackgroundResource(R.drawable.rounded_button_background)
            
            // 벨 선택 시 범위를 0~5000으로 변경
            priceRangeSlider.valueTo = 5000f
            priceRangeSlider.setValues(0f, 5000f)
            priceRangeText.text = "가격 범위: 0 - 5000"
            
            currentPriceFilter = PriceFilter(
                currencyType = CurrencyType.BELLS,
                minPrice = 0,
                maxPrice = 5000
            )
            applyFilters()
        }
        
        btnMiles.setOnClickListener {
            btnMiles.setBackgroundResource(R.drawable.rounded_button_selected)
            btnBells.setBackgroundResource(R.drawable.rounded_button_background)
            
            // 마일 선택 시 범위를 0~1000으로 변경
            priceRangeSlider.valueTo = 1000f
            priceRangeSlider.setValues(0f, 1000f)
            priceRangeText.text = "가격 범위: 0 - 1000"
            
            currentPriceFilter = PriceFilter(
                currencyType = CurrencyType.MILES,
                minPrice = 0,
                maxPrice = 1000
            )
            applyFilters()
        }
        
        dialog.show()
    }
    
    // 색상 목록 생성
    private fun createColorList(): List<ColorItem> {
        return listOf(
            ColorItem("필터 해제", android.graphics.Color.TRANSPARENT, "", true),
            ColorItem("빨강", android.graphics.Color.RED, "빨강"),
            ColorItem("파랑", android.graphics.Color.BLUE, "파랑"),
            ColorItem("초록", android.graphics.Color.GREEN, "초록"),
            ColorItem("노랑", android.graphics.Color.YELLOW, "노랑"),
            ColorItem("주황", android.graphics.Color.rgb(255, 165, 0), "주황"),
            ColorItem("보라", android.graphics.Color.rgb(128, 0, 128), "보라"),
            ColorItem("분홍", android.graphics.Color.rgb(255, 192, 203), "분홍"),
            ColorItem("갈색", android.graphics.Color.rgb(139, 69, 19), "갈색"),
            ColorItem("검정", android.graphics.Color.BLACK, "검정"),
            ColorItem("하양", android.graphics.Color.WHITE, "하양"),
            ColorItem("회색", android.graphics.Color.GRAY, "회색"),
            ColorItem("하늘색", android.graphics.Color.rgb(135, 206, 235), "하늘색"),
            ColorItem("남색", android.graphics.Color.rgb(0, 0, 128), "남색"),
            ColorItem("베이지", android.graphics.Color.rgb(245, 245, 220), "베이지"),
            ColorItem("와인 레드", android.graphics.Color.rgb(139, 0, 0), "와인 레드"),
            ColorItem("코랄", android.graphics.Color.rgb(255, 127, 80), "코랄"),
            ColorItem("아이보리", android.graphics.Color.rgb(255, 255, 240), "아이보리")
        )
    }
    
    // 아이템 필터링
    private fun filterItems(tag: ItemTag?) {
        currentFilter = tag
        applyFilters()
    }
    
    // 필터와 검색을 모두 적용
    private fun applyFilters() {
        var filteredGroups: List<ItemGroup> = globalItemGroups

        // 태그 필터 적용
        if (currentFilter != null) {
            filteredGroups = filteredGroups.filter { it.tag == currentFilter }
        }

        // 검색어 필터 적용
        if (currentSearchQuery.isNotEmpty()) {
            filteredGroups = filteredGroups.filter { group ->
                group.title.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // 색상 필터 적용
        if (currentColorFilter != null) {
            android.util.Log.d("Tab1Fragment", "색상 필터 적용: $currentColorFilter")
            filteredGroups = filteredGroups.filter { group ->
                group.items.any { item ->
                    val matches = item.color == currentColorFilter ||
                        item.color.contains(currentColorFilter!!, ignoreCase = true) ||
                        getEnglishColorName(currentColorFilter!!)?.let { englishName ->
                            item.color.contains(englishName, ignoreCase = true)
                        } ?: false
                    
                    if (matches) {
                        android.util.Log.d("Tab1Fragment", "색상 매칭: ${item.color} == $currentColorFilter")
                    }
                    matches
                }
            }
            android.util.Log.d("Tab1Fragment", "색상 필터링 후 아이템 그룹 수: ${filteredGroups.size}")
        }

        // 가격 필터 적용
        if (currentPriceFilter != null) {
            android.util.Log.d("Tab1Fragment", "가격 필터 적용: ${currentPriceFilter}")
            filteredGroups = filteredGroups.filter { group ->
                group.items.any { item ->
                    val priceString = when (currentPriceFilter!!.currencyType) {
                        CurrencyType.BELLS -> group.price_bell
                        CurrencyType.MILES -> group.price_mile
                    }
                    
                    // 숫자가 아닌 경우 (예: "비매품") 필터링에서 제외
                    val cleanPriceString = priceString.replace(",", "").replace("벨", "").replace("마일", "").trim()
                    val price = cleanPriceString.toIntOrNull()
                    
                    if (price == null) {
                        // 숫자가 아닌 경우 (비매품 등) 필터링에서 제외
                        android.util.Log.d("Tab1Fragment", "숫자가 아닌 가격 제외: $priceString")
                        false
                    } else {
                        val matches = price >= currentPriceFilter!!.minPrice && price <= currentPriceFilter!!.maxPrice
                        
                        if (matches) {
                            android.util.Log.d("Tab1Fragment", "가격 매칭: $price (${currentPriceFilter!!.minPrice}-${currentPriceFilter!!.maxPrice})")
                        }
                        matches
                    }
                }
            }
            android.util.Log.d("Tab1Fragment", "가격 필터링 후 아이템 그룹 수: ${filteredGroups.size}")
        }

        // 어댑터 데이터만 업데이트 (재생성하지 않음)
        adapter.updateData(filteredGroups)
        adapter.setColorFilter(currentColorFilter)
        updateButtonStyles(currentFilter)
        updateColorButtonStyle()
        updatePriceButtonStyle()
    }
    
    // 버튼 스타일 업데이트
    private fun updateButtonStyles(selectedTag: ItemTag?) {
        btnAll.isSelected = (selectedTag == null)
        btnTop.isSelected = (selectedTag == ItemTag.TOP)
        btnBottom.isSelected = (selectedTag == ItemTag.BOTTOM)
        btnHat.isSelected = (selectedTag == ItemTag.HAT)
        btnShoes.isSelected = (selectedTag == ItemTag.SHOES)
    }

    private fun highlightButton(button: Button) {
        button.setBackgroundResource(R.drawable.rounded_top_corners_white)  // 누른 버튼 색깔 나중에 강조하는걸로 바꾸기
        button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
    }
    
    // 색상 버튼 스타일 업데이트
    private fun updateColorButtonStyle() {
        if (currentColorFilter != null) {
            // 색상 필터가 적용된 경우 버튼을 강조
            colorButton.alpha = 0.7f
            colorButton.setBackgroundResource(R.drawable.circle_background_blue)
        } else {
            // 색상 필터가 없는 경우 기본 스타일
            colorButton.alpha = 1.0f
            colorButton.setBackgroundResource(R.drawable.circle_background_white)
        }
    }
    
    // 가격 버튼 스타일 업데이트
    private fun updatePriceButtonStyle() {
        if (currentPriceFilter != null) {
            // 가격 필터가 적용된 경우 버튼을 강조
            priceButton.alpha = 0.7f
            priceButton.setBackgroundResource(R.drawable.circle_background_green)
        } else {
            // 가격 필터가 없는 경우 기본 스타일
            priceButton.alpha = 1.0f
            priceButton.setBackgroundResource(R.drawable.circle_background_white)
        }
    }
    
    // 한국어 색상명을 영어 색상명으로 변환
    private fun getEnglishColorName(koreanColor: String): String? {
        return when (koreanColor) {
            "빨강" -> "red"
            "파랑" -> "blue"
            "초록" -> "green"
            "노랑" -> "yellow"
            "주황" -> "orange"
            "보라" -> "purple"
            "분홍" -> "pink"
            "갈색" -> "brown"
            "검정" -> "black"
            "하양" -> "white"
            "회색" -> "grey"
            "하늘색" -> "skyblue"
            "남색" -> "navy"
            "베이지" -> "beige"
            "와인 레드" -> "winered"
            "코랄" -> "coral"
            "아이보리" -> "ivory"
            else -> null
        }
    }

    
    
    // 빌라저 설정
    private fun setupVillager() {
        try {
            // VillagerLoader를 사용하여 동적으로 villager 정보 로드
            val loadedVillagers = VillagerLoader.loadVillagersFromAssets(requireContext())
            villagerList.clear()
            villagerList.addAll(loadedVillagers)

            // 이전에 선택된 빌라저가 있는지 확인
            val previouslySelectedVillager = SelectedCharacterManager.getSelectedVillager()

            if (previouslySelectedVillager == null) {
                // 이전에 선택된 빌라저가 없으면 첫 번째 빌라저를 기본으로 설정하고 저장
                villagerList.firstOrNull()?.let { 
                    SelectedCharacterManager.setSelectedVillager(it)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // 가상 캔버스로 캐릭터 렌더링
    private fun renderCharacterOnVirtualCanvas() {
        val selectedVillager = SelectedCharacterManager.getSelectedVillager()
        val (selectedHat, selectedHatGroup) = SelectedItemsManager.getSelectedHat()
        val (selectedTop, selectedTopGroup) = SelectedItemsManager.getSelectedTop()
        val (selectedBottom, selectedBottomGroup) = SelectedItemsManager.getSelectedBottom()
        val (selectedShoes, selectedShoesGroup) = SelectedItemsManager.getSelectedShoes()
        
        // 가상 캔버스에서 렌더링
        val renderedBitmap = virtualCanvas.renderCharacter(
            selectedVillager,
            selectedHat to selectedHatGroup,
            selectedTop to selectedTopGroup,
            selectedBottom to selectedBottomGroup,
            selectedShoes to selectedShoesGroup
        )
        
        // 렌더링된 결과를 ImageView에 표시
        virtualCanvasImageView.setImageBitmap(renderedBitmap)
        virtualCanvasImageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    

    

    

    
    // assets 폴더에서 이미지 불러오기
    private fun loadImageFromAssets(fileName: String, imageView: ImageView) {
        try {
            val inputStream = requireContext().assets.open(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    


    // 아이템 선택 처리
    private fun onItemSelected(item: Item, group: ItemGroup) {
        // 현재 착용 중인 아이템인지 확인
        val (currentlyEquippedItem, _) = when (group.tag) {
            ItemTag.HAT -> SelectedItemsManager.getSelectedHat()
            ItemTag.TOP -> SelectedItemsManager.getSelectedTop()
            ItemTag.BOTTOM -> SelectedItemsManager.getSelectedBottom()
            ItemTag.SHOES -> SelectedItemsManager.getSelectedShoes()
        }

        // 이미 착용 중인 아이템을 다시 클릭한 경우, 해제
        if (currentlyEquippedItem == item) {
            unequipItem(group.tag)
            return // 해제 후 함수 종료
        }

        // 다른 아이템을 선택한 경우, 기존의 장착 로직 수행
        when (group.tag) {
            ItemTag.HAT -> {
                SelectedItemsManager.setSelectedHat(item, group)
                loadImageFromAssets(item.imagePath, equippedHatImage)
                equippedHatImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.TOP -> {
                SelectedItemsManager.setSelectedTop(item, group)
                loadImageFromAssets(item.imagePath, equippedTopImage)
                equippedTopImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.BOTTOM -> {
                SelectedItemsManager.setSelectedBottom(item, group)
                loadImageFromAssets(item.imagePath, equippedBottomImage)
                equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.SHOES -> {
                SelectedItemsManager.setSelectedShoes(item, group)
                loadImageFromAssets(item.imagePath, equippedShoesImage)
                equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        updateSelectedItemIndex(item, group)
        adapter.notifyDataSetChanged()
        
        // 가상 캔버스 업데이트
        renderCharacterOnVirtualCanvas()
    }

    private fun unequipItem(itemTag: ItemTag) {
        when (itemTag) {
            ItemTag.HAT -> {
                SelectedItemsManager.clearSelectedHat()
                equippedHatImage.setImageResource(R.drawable.ic_add) // 우측 UI 기본 이미지로 변경
                equippedHatImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedHatImage.imageTintList = null
            }
            ItemTag.TOP -> {
                SelectedItemsManager.clearSelectedTop()
                equippedTopImage.setImageResource(R.drawable.ic_add)
                equippedTopImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedTopImage.imageTintList = null
            }
            ItemTag.BOTTOM -> {
                SelectedItemsManager.clearSelectedBottom()
                equippedBottomImage.setImageResource(R.drawable.ic_add)
                equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedBottomImage.imageTintList = null
            }
            ItemTag.SHOES -> {
                SelectedItemsManager.clearSelectedShoes()
                equippedShoesImage.setImageResource(R.drawable.ic_add)
                equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedShoesImage.imageTintList = null
            }
        }
        // UI 갱신이 필요하다면 추가 로직
        adapter.notifyDataSetChanged()
        
        // 가상 캔버스 업데이트
        renderCharacterOnVirtualCanvas()
    }
    
    // 선택된 아이템의 인덱스를 adapter에 저장
    private fun updateSelectedItemIndex(selectedItem: Item, selectedGroup: ItemGroup) {
        val selectedIndex = selectedGroup.items.indexOfFirst { 
            it.color == selectedItem.color && it.imagePath == selectedItem.imagePath 
        }
        if (selectedIndex != -1) {
            adapter.updateCurrentIndex(selectedGroup, selectedIndex)
        }
    }
    

    
    // 검색 기능 설정
    private fun setupSearchFunction() {
        // 한글 입력을 위한 설정
        searchEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
        
        // 키보드 동작 설정
        searchEditText.imeOptions = android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
        
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s?.toString() ?: ""
                applyFilters()
            }
        })
        
        // 검색 버튼 클릭 시 키보드 숨기기
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                searchEditText.clearFocus()
                val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 가상 캔버스 리소스 정리
        virtualCanvas.cleanup()
    }
} 