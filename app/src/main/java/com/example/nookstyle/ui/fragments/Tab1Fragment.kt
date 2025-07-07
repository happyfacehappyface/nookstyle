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
import java.io.IOException



class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemGroupAdapter

    private lateinit var imageVillager: ImageView
    private lateinit var imageVillagerHead: ImageView
    private lateinit var imageShoes: ImageView
    private lateinit var imageBottom: ImageView
    private lateinit var imageTop: ImageView
    private lateinit var imageHat: ImageView
    
    // 기본 의류 이미지들
    private lateinit var defaultShoes: ImageView
    private lateinit var defaultBottom: ImageView
    private lateinit var defaultTop: ImageView
    private lateinit var defaultHat: ImageView

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
    
    // 필터링 상태
    private var currentFilter: ItemTag? = null
    private var currentSearchQuery: String = ""
    private var currentColorFilter: String? = null

    // 현재 빌라저
    private var currentVillager: Villager? = null

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
        imageVillager = view.findViewById(R.id.imageVillager)
        imageVillagerHead = view.findViewById(R.id.imageVillagerHead)
        imageShoes = view.findViewById(R.id.imageShoes)
        imageBottom = view.findViewById(R.id.imageBottom)
        imageTop = view.findViewById(R.id.imageTop)
        imageHat = view.findViewById(R.id.imageHat)
        
        // 기본 의류 이미지들 초기화
        defaultShoes = view.findViewById(R.id.defaultShoes)
        defaultBottom = view.findViewById(R.id.defaultBottom)
        defaultTop = view.findViewById(R.id.defaultTop)
        defaultHat = view.findViewById(R.id.defaultHat)

        // 캐릭터 버튼 초기화
        chooseCharacter = view.findViewById(R.id.chooseCharacter)
        chooseCharacter.setOnClickListener {
            showCharacterSelectDialog()
        }

        // 착용 중인 아이템 표시 이미지들 초기화
        equippedHatImage = view.findViewById(R.id.equippedHatImage)
        equippedTopImage = view.findViewById(R.id.equippedTopImage)
        equippedBottomImage = view.findViewById(R.id.equippedBottomImage)
        equippedShoesImage = view.findViewById(R.id.equippedShoesImage)

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
        
        // 기본 의류 이미지 로드
        loadDefaultClothingImages()
        
        // 겹쳐진 이미지 설정
        setupOverlappingImages()
        
        // 뷰가 완전히 그려진 후 이미지 스타일 설정
        view?.post {
            setupImageStyles()
            updateDefaultClothingVisibility()
        }
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
            currentVillager = selectedVillager
            
            // villager 이미지 변경
            loadVillagerImages(selectedVillager)
            
            // 기본 의류 이미지 로드
            loadDefaultClothingImages()
            
            // 여기에서 rotate 버튼 표시 여부 결정
            
            // 선택한 후 재세팅
            setupOverlappingImages()
            
            // 뷰가 완전히 업데이트된 후 의류 위치와 스케일 조정
            view?.post {
                setupImageStyles()
                updateDefaultClothingVisibility()
            }
            
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
            // villager 전용 캡처 컨테이너 스크린샷 (주변 UI 제외)
            val villagerContainer = view?.findViewById<FrameLayout>(R.id.villagerCaptureContainer)
            villagerContainer?.let { container ->
                // 뷰가 완전히 그려진 후 스크린샷 찍기
                container.post {
                    val savedPath = ScreenshotUtil.captureAndSaveView(requireContext(), container, fileName)
                    if (savedPath != null) {
                        Toast.makeText(context, "스크린샷이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "스크린샷 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
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
                group.title.contains(currentSearchQuery, ignoreCase = true) ||
                group.items.any { item ->
                    item.color.contains(currentSearchQuery, ignoreCase = true)
                } ||
                group.price_bell.contains(currentSearchQuery, ignoreCase = true) ||
                group.price_mile.contains(currentSearchQuery, ignoreCase = true)
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

        // 어댑터 데이터만 업데이트 (재생성하지 않음)
        adapter.updateData(filteredGroups)
        updateButtonStyles(currentFilter)
        updateColorButtonStyle()
    }
    
    // 버튼 스타일 업데이트
    private fun updateButtonStyles(selectedTag: ItemTag?) {
        // 모든 버튼을 기본 스타일로 초기화
        listOf(btnAll, btnTop, btnBottom, btnHat, btnShoes).forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        }

        // 선택된 버튼만 강조
        when (selectedTag) {
            null -> highlightButton(btnAll)
            ItemTag.TOP -> highlightButton(btnTop)
            ItemTag.BOTTOM -> highlightButton(btnBottom)
            ItemTag.HAT -> highlightButton(btnHat)
            ItemTag.SHOES -> highlightButton(btnShoes)
        }
    }

    private fun highlightButton(button: Button) {
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
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

            // 첫 번째 villager를 기본으로 설정
            currentVillager = villagerList.firstOrNull()
            currentVillager?.let { loadVillagerImages(it) }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    
    private fun setupOverlappingImages() {
        // 빌라저 기본 이미지 로드 (동적으로 로드된 villager 사용)
        currentVillager?.let { villager ->
            loadVillagerImages(villager)
        }

        // 저장된 선택된 아이템들 복원
        val (selectedTop, selectedTopGroup) = SelectedItemsManager.getSelectedTop()
        val (selectedBottom, selectedBottomGroup) = SelectedItemsManager.getSelectedBottom()
        val (selectedHat, selectedHatGroup) = SelectedItemsManager.getSelectedHat()
        val (selectedShoes, selectedShoesGroup) = SelectedItemsManager.getSelectedShoes()

        if (selectedTop != null) {
            loadImageFromAssets(selectedTop.imagePath, imageTop)
            loadImageFromAssets(selectedTop.imagePath, equippedTopImage)
            equippedTopImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageTop.setImageDrawable(null)
            equippedTopImage.setImageResource(R.drawable.ic_add)
            equippedTopImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            equippedTopImage.imageTintList = null
        }

        if (selectedBottom != null) {
            loadImageFromAssets(selectedBottom.imagePath, imageBottom)
            loadImageFromAssets(selectedBottom.imagePath, equippedBottomImage)
            equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageBottom.setImageDrawable(null)
            equippedBottomImage.setImageResource(R.drawable.ic_add)
            equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            equippedBottomImage.imageTintList = null
        }

        if (selectedHat != null) {
            loadImageFromAssets(selectedHat.imagePath, imageHat)
            loadImageFromAssets(selectedHat.imagePath, equippedHatImage)
            equippedHatImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageHat.setImageDrawable(null)
            equippedHatImage.setImageResource(R.drawable.ic_add)
            equippedHatImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            equippedHatImage.imageTintList = null
        }

        if (selectedShoes != null) {
            loadImageFromAssets(selectedShoes.imagePath, imageShoes)
            loadImageFromAssets(selectedShoes.imagePath, equippedShoesImage)
            equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageShoes.setImageDrawable(null)
            equippedShoesImage.setImageResource(R.drawable.ic_add)
            equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            equippedShoesImage.imageTintList = null
        }
    }
    
    // 이미지 스타일 설정
    private fun setupImageStyles() {
        currentVillager?.let { villager ->
            // 빌라저 remain 이미지와 head 이미지를 완전히 동일한 크기로 설정
            val villagerSize = 200
            setupImageStyle(imageVillager, villagerSize, villagerSize, 0, 0, android.R.color.transparent)
            setupImageStyle(imageVillagerHead, villagerSize, villagerSize, 0, 0, android.R.color.transparent)
            
            // 기본 신발 이미지 - villager와 동일한 크기
            setupImageStyle(defaultShoes, 200, 200, 0, 0, android.R.color.transparent)
            
            // 신발 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageShoes, villager.shoesPosition, 120, 120)
            
            // 기본 하의 이미지 - villager와 동일한 크기
            setupImageStyle(defaultBottom, 200, 200, 0, 0, android.R.color.transparent)
            
            // 하의 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageBottom, villager.bottomPosition, 100, 100)
            
            // 기본 상의 이미지 - villager와 동일한 크기
            setupImageStyle(defaultTop, 200, 200, 0, 0, android.R.color.transparent)
            
            // 상의 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageTop, villager.topPosition, 80, 80)
            
            // 기본 모자 이미지 - villager와 동일한 크기
            setupImageStyle(defaultHat, 200, 200, 0, 0, android.R.color.transparent)
            
            // 모자 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageHat, villager.hatPosition, 60, 60)
        }
    }
    
    // 개별 이미지 스타일 설정
    private fun setupImageStyle(
        imageView: ImageView, 
        width: Int, 
        height: Int, 
        marginTop: Int, 
        marginStart: Int, 
        backgroundColor: Int
    ) {
        // 크기 설정
        val layoutParams = imageView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = (width * resources.displayMetrics.density).toInt()
        layoutParams.height = (height * resources.displayMetrics.density).toInt()
        
        // 위치 설정
        layoutParams.topMargin = (marginTop * resources.displayMetrics.density).toInt()
        layoutParams.leftMargin = (marginStart * resources.displayMetrics.density).toInt()
        
        // 배경 설정
        imageView.setBackgroundColor(ContextCompat.getColor(requireContext(), backgroundColor))
        
        // 스케일 타입 설정 - 빌라저와 기본 의류 이미지는 FIT_CENTER로 설정하여 잘리지 않도록
        if (imageView == imageVillager || imageView == imageVillagerHead || imageView == defaultShoes || imageView == defaultBottom || imageView == defaultTop || imageView == defaultHat) {
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        } else {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        
        // 패딩 설정
        val padding = (8 * resources.displayMetrics.density).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        
        // 레이아웃 파라미터 적용
        imageView.layoutParams = layoutParams
    }
    
    // 의류 이미지 스타일 설정 (ClothingPosition과 ItemGroup 값 중복 적용)
    private fun setupClothingImageStyle(
        imageView: ImageView,
        position: ClothingPosition,
        baseWidth: Int,
        baseHeight: Int
    ) {
        // 현재 선택된 아이템 그룹의 위치/스케일 값 가져오기
        val itemGroupAdjustment = getCurrentItemGroupAdjustment(imageView)
        
        // villager의 기본 위치와 ItemGroup의 조정값을 중복 적용
        val adjustedScaleX = position.scaleX * itemGroupAdjustment.scaleX
        val adjustedScaleY = position.scaleY * itemGroupAdjustment.scaleY
        
        val scaledWidth = (baseWidth * adjustedScaleX * resources.displayMetrics.density).toInt()
        val scaledHeight = (baseHeight * adjustedScaleY * resources.displayMetrics.density).toInt()

        val layoutParams = imageView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = scaledWidth
        layoutParams.height = scaledHeight

        // 중심 위치로 이동 (부모 FrameLayout 기준) + ItemGroup 조정값 적용
        val parent = imageView.parent as View
        val parentWidth = parent.width
        val parentHeight = parent.height
        if (parentWidth > 0 && parentHeight > 0) {
            val adjustedX = position.x + itemGroupAdjustment.x
            val adjustedY = position.y + itemGroupAdjustment.y
            layoutParams.leftMargin = (parentWidth * adjustedX - scaledWidth / 2).toInt()
            layoutParams.topMargin = (parentHeight * adjustedY - scaledHeight / 2).toInt()
        }


        layoutParams.gravity = android.view.Gravity.TOP or android.view.Gravity.START
        imageView.layoutParams = layoutParams

        // 스케일, 회전 적용
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.rotation = position.rotation
        imageView.setPadding(0, 0, 0, 0)
    }
    
    // 기본 의류 이미지 스타일 설정 (고정 크기 및 위치)
    private fun setupDefaultClothingImageStyle(
        imageView: ImageView,
        baseWidth: Int,
        baseHeight: Int
    ) {
        val scaledWidth = (baseWidth * resources.displayMetrics.density).toInt()
        val scaledHeight = (baseHeight * resources.displayMetrics.density).toInt()

        val layoutParams = imageView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = scaledWidth
        layoutParams.height = scaledHeight

        // 부모 FrameLayout의 중심에 고정 배치
        val parent = imageView.parent as View
        val parentWidth = parent.width
        val parentHeight = parent.height
        if (parentWidth > 0 && parentHeight > 0) {
            layoutParams.leftMargin = (parentWidth - scaledWidth) / 2
            layoutParams.topMargin = (parentHeight - scaledHeight) / 2
        }

        layoutParams.gravity = android.view.Gravity.TOP or android.view.Gravity.START
        imageView.layoutParams = layoutParams

        // 스케일 타입 설정
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.rotation = 0f // 회전 없음
        imageView.setPadding(0, 0, 0, 0)
    }
    
    // 기본 복장 설정 함수들
    private fun setupDefaultTop() {
        // 기본 상의: 2번공 옷 (파랑색)
        val topGroup = globalItemGroups.find { it.title == "2번공 옷" }
        val defaultTop = topGroup?.items?.find { it.color == "파랑" }
        if (defaultTop != null && topGroup != null) {
            SelectedItemsManager.setSelectedTop(defaultTop, topGroup)
            loadImageFromAssets(defaultTop.imagePath, imageTop)
            loadImageFromAssets(defaultTop.imagePath, equippedTopImage)
        } else {
            // 2번공 옷이 없으면 첫 번째 상의 사용
            val fallbackTopGroup = globalItemGroups.find { it.tag == ItemTag.TOP }
            val fallbackTop = fallbackTopGroup?.items?.firstOrNull()
            if (fallbackTop != null && fallbackTopGroup != null) {
                SelectedItemsManager.setSelectedTop(fallbackTop, fallbackTopGroup)
                loadImageFromAssets(fallbackTop.imagePath, imageTop)
                loadImageFromAssets(fallbackTop.imagePath, equippedTopImage)
            }
        }
    }
    
    private fun setupDefaultBottom() {
        // 기본 하의: 가죽 바지 (검정색)
        val bottomGroup = globalItemGroups.find { it.title == "가죽 바지" }
        val defaultBottom = bottomGroup?.items?.find { it.color == "검정" }
        if (defaultBottom != null && bottomGroup != null) {
            SelectedItemsManager.setSelectedBottom(defaultBottom, bottomGroup)
            loadImageFromAssets(defaultBottom.imagePath, imageBottom)
            loadImageFromAssets(defaultBottom.imagePath, equippedBottomImage)
        } else {
            // 가죽 바지가 없으면 첫 번째 하의 사용
            val fallbackBottomGroup = globalItemGroups.find { it.tag == ItemTag.BOTTOM }
            val fallbackBottom = fallbackBottomGroup?.items?.firstOrNull()
            if (fallbackBottom != null && fallbackBottomGroup != null) {
                SelectedItemsManager.setSelectedBottom(fallbackBottom, fallbackBottomGroup)
                loadImageFromAssets(fallbackBottom.imagePath, imageBottom)
                loadImageFromAssets(fallbackBottom.imagePath, equippedBottomImage)
            }
        }
    }
    
    private fun setupDefaultHat() {
        // 기본 모자: 개구리 모자 (초록색)
        val hatGroup = globalItemGroups.find { it.title == "마리오 모자" }
        val defaultHat = hatGroup?.items?.find { it.color == "빨강" }
        if (defaultHat != null && hatGroup != null) {
            SelectedItemsManager.setSelectedHat(defaultHat, hatGroup)
            loadImageFromAssets(defaultHat.imagePath, imageHat)
            loadImageFromAssets(defaultHat.imagePath, equippedHatImage)
        } else {
            // 개구리 모자가 없으면 첫 번째 모자 사용
            val fallbackHatGroup = globalItemGroups.find { it.tag == ItemTag.HAT }
            val fallbackHat = fallbackHatGroup?.items?.firstOrNull()
            if (fallbackHat != null && fallbackHatGroup != null) {
                SelectedItemsManager.setSelectedHat(fallbackHat, fallbackHatGroup)
                loadImageFromAssets(fallbackHat.imagePath, imageHat)
                loadImageFromAssets(fallbackHat.imagePath, equippedHatImage)
            }
        }
    }
    
    private fun setupDefaultShoes() {
        // 기본 신발: 가죽 스니커 (하양색)
        val shoesGroup = globalItemGroups.find { it.title == "가죽 스니커" }
        val defaultShoes = shoesGroup?.items?.find { it.color == "하양" }
        if (defaultShoes != null && shoesGroup != null) {
            SelectedItemsManager.setSelectedShoes(defaultShoes, shoesGroup)
            loadImageFromAssets(defaultShoes.imagePath, imageShoes)
            loadImageFromAssets(defaultShoes.imagePath, equippedShoesImage)
        } else {
            // 가죽 스니커가 없으면 첫 번째 신발 사용
            val fallbackShoesGroup = globalItemGroups.find { it.tag == ItemTag.SHOES }
            val fallbackShoes = fallbackShoesGroup?.items?.firstOrNull()
            if (fallbackShoes != null && fallbackShoesGroup != null) {
                SelectedItemsManager.setSelectedShoes(fallbackShoes, fallbackShoesGroup)
                loadImageFromAssets(fallbackShoes.imagePath, imageShoes)
                loadImageFromAssets(fallbackShoes.imagePath, equippedShoesImage)
            }
        }
    }
    
    // 현재 선택된 아이템 그룹의 위치/스케일 조정값 가져오기
    private fun getCurrentItemGroupAdjustment(imageView: ImageView): ItemGroupAdjustment {
        return when (imageView) {
            imageHat -> {
                val (_, selectedGroup) = SelectedItemsManager.getSelectedHat()
                selectedGroup?.let { group ->
                    ItemGroupAdjustment(group.x, group.y, group.scaleX, group.scaleY)
                } ?: ItemGroupAdjustment(0f, 0f, 1.0f, 1.0f)
            }
            imageTop -> {
                val (_, selectedGroup) = SelectedItemsManager.getSelectedTop()
                selectedGroup?.let { group ->
                    ItemGroupAdjustment(group.x, group.y, group.scaleX, group.scaleY)
                } ?: ItemGroupAdjustment(0f, 0f, 1.0f, 1.0f)
            }
            imageBottom -> {
                val (_, selectedGroup) = SelectedItemsManager.getSelectedBottom()
                selectedGroup?.let { group ->
                    ItemGroupAdjustment(group.x, group.y, group.scaleX, group.scaleY)
                } ?: ItemGroupAdjustment(0f, 0f, 1.0f, 1.0f)
            }
            imageShoes -> {
                val (_, selectedGroup) = SelectedItemsManager.getSelectedShoes()
                selectedGroup?.let { group ->
                    ItemGroupAdjustment(group.x, group.y, group.scaleX, group.scaleY)
                } ?: ItemGroupAdjustment(0f, 0f, 1.0f, 1.0f)
            }
            else -> ItemGroupAdjustment(0f, 0f, 1.0f, 1.0f)
        }
    }
    
    // ItemGroup 조정값을 담는 데이터 클래스
    private data class ItemGroupAdjustment(
        val x: Float,
        val y: Float,
        val scaleX: Float,
        val scaleY: Float
    )
    
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
    
    // assets 폴더에서 이미지가 존재하는 경우에만 불러오기
    private fun loadImageFromAssetsIfExists(fileName: String, imageView: ImageView) {
        try {
            // 먼저 파일이 존재하는지 확인
            val inputStream = requireContext().assets.open(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
            inputStream.close()
            
            // 이미지가 성공적으로 로드되면 태그를 설정하여 존재함을 표시
            imageView.tag = true
        } catch (e: IOException) {
            // 파일이 존재하지 않으면 이미지를 지우고 태그를 false로 설정
            imageView.setImageDrawable(null)
            imageView.tag = false
        }
    }
    
    // villager 이미지들 로드 (remain.png와 head.png)
    private fun loadVillagerImages(villager: Villager) {
        val folderPath = villager.imagePath.substringBeforeLast("/")
        
        // remain.png 로드 (기본 villager 이미지)
        loadImageFromAssetsIfExists("$folderPath/remain.png", imageVillager)
        
        // head.png 로드 (head 이미지)
        loadImageFromAssetsIfExists("$folderPath/head.png", imageVillagerHead)
    }
    
    // 기본 의류 이미지 로드
    private fun loadDefaultClothingImages() {
        currentVillager?.let { villager ->
            val folderPath = villager.imagePath.substringBeforeLast("/")
            
            // 기본 의류 이미지들 로드 (존재하는 경우에만)
            loadImageFromAssetsIfExists("$folderPath/shoes.png", defaultShoes)
            loadImageFromAssetsIfExists("$folderPath/bottom.png", defaultBottom)
            loadImageFromAssetsIfExists("$folderPath/top.png", defaultTop)
            loadImageFromAssetsIfExists("$folderPath/hat.png", defaultHat)
        }
    }
    
    // 기본 의류 이미지 표시/숨김 관리
    private fun updateDefaultClothingVisibility() {
        val (selectedTop, _) = SelectedItemsManager.getSelectedTop()
        val (selectedBottom, _) = SelectedItemsManager.getSelectedBottom()
        val (selectedHat, _) = SelectedItemsManager.getSelectedHat()
        val (selectedShoes, _) = SelectedItemsManager.getSelectedShoes()
        
        // 착용된 의류가 없고, 기본 이미지가 존재할 때만 기본 의류 이미지 표시
        defaultTop.visibility = if (selectedTop == null && defaultTop.tag == true) View.VISIBLE else View.GONE
        defaultBottom.visibility = if (selectedBottom == null && defaultBottom.tag == true) View.VISIBLE else View.GONE
        defaultHat.visibility = if (selectedHat == null && defaultHat.tag == true) View.VISIBLE else View.GONE
        defaultShoes.visibility = if (selectedShoes == null && defaultShoes.tag == true) View.VISIBLE else View.GONE
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
                loadImageFromAssets(item.imagePath, imageHat)
                loadImageFromAssets(item.imagePath, equippedHatImage)
                equippedHatImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.TOP -> {
                SelectedItemsManager.setSelectedTop(item, group)
                loadImageFromAssets(item.imagePath, imageTop)
                loadImageFromAssets(item.imagePath, equippedTopImage)
                equippedTopImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.BOTTOM -> {
                SelectedItemsManager.setSelectedBottom(item, group)
                loadImageFromAssets(item.imagePath, imageBottom)
                loadImageFromAssets(item.imagePath, equippedBottomImage)
                equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            ItemTag.SHOES -> {
                SelectedItemsManager.setSelectedShoes(item, group)
                loadImageFromAssets(item.imagePath, imageShoes)
                loadImageFromAssets(item.imagePath, equippedShoesImage)
                equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        updateSelectedItemIndex(item, group)
        adapter.notifyDataSetChanged()
        view?.post {
            setupImageStyles()
            updateDefaultClothingVisibility()
        }
    }

    private fun unequipItem(itemTag: ItemTag) {
        when (itemTag) {
            ItemTag.HAT -> {
                SelectedItemsManager.clearSelectedHat()
                imageHat.setImageDrawable(null) // 캐릭터 이미지에서 해당 아이템 제거
                equippedHatImage.setImageResource(R.drawable.ic_add) // 우측 UI 기본 이미지로 변경
                equippedHatImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedHatImage.imageTintList = null
            }
            ItemTag.TOP -> {
                SelectedItemsManager.clearSelectedTop()
                imageTop.setImageDrawable(null)
                equippedTopImage.setImageResource(R.drawable.ic_add)
                equippedTopImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedTopImage.imageTintList = null
            }
            ItemTag.BOTTOM -> {
                SelectedItemsManager.clearSelectedBottom()
                imageBottom.setImageDrawable(null)
                equippedBottomImage.setImageResource(R.drawable.ic_add)
                equippedBottomImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedBottomImage.imageTintList = null
            }
            ItemTag.SHOES -> {
                SelectedItemsManager.clearSelectedShoes()
                imageShoes.setImageDrawable(null)
                equippedShoesImage.setImageResource(R.drawable.ic_add)
                equippedShoesImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                equippedShoesImage.imageTintList = null
            }
        }
        // UI 갱신이 필요하다면 추가 로직
        adapter.notifyDataSetChanged()
        view?.post {
            updateDefaultClothingVisibility()
        }
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
} 