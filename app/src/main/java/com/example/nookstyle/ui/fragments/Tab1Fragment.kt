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
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.*
import com.example.nookstyle.ui.adapter.ItemGroupAdapter
import com.example.nookstyle.util.AssetItemLoader
import com.example.nookstyle.util.ScreenshotUtil
import java.io.IOException



class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemGroupAdapter

    private lateinit var imageVillager: ImageView
    private lateinit var imageShoes: ImageView
    private lateinit var imageBottom: ImageView
    private lateinit var imageTop: ImageView
    private lateinit var imageHat: ImageView
    
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
    
    // 필터링 상태
    private var currentFilter: ItemTag? = null
    private var currentSearchQuery: String = ""
    
    // 현재 빌라저
    private var currentVillager: Villager? = null
    
    // 현재 선택된 아이템들
    private var selectedHat: Item? = null
    private var selectedTop: Item? = null
    private var selectedBottom: Item? = null
    private var selectedShoes: Item? = null
    
    // 현재 선택된 아이템 그룹들
    private var selectedHatGroup: ItemGroup? = null
    private var selectedTopGroup: ItemGroup? = null
    private var selectedBottomGroup: ItemGroup? = null
    private var selectedShoesGroup: ItemGroup? = null
    


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
        imageShoes = view.findViewById(R.id.imageShoes)
        imageBottom = view.findViewById(R.id.imageBottom)
        imageTop = view.findViewById(R.id.imageTop)
        imageHat = view.findViewById(R.id.imageHat)
        
        // 착용 중인 아이템 표시 이미지들 초기화
        equippedHatImage = view.findViewById(R.id.equippedHatImage)
        equippedTopImage = view.findViewById(R.id.equippedTopImage)
        equippedBottomImage = view.findViewById(R.id.equippedBottomImage)
        equippedShoesImage = view.findViewById(R.id.equippedShoesImage)
        
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
        
        // 겹쳐진 이미지 설정
        setupOverlappingImages()
        
        // 뷰가 완전히 그려진 후 이미지 스타일 설정
        view?.post {
            setupImageStyles()
        }
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
        
        // 기본 파일명 설정 (현재 시간 기준)
        val defaultFileName = "Villager_Outfit_${System.currentTimeMillis()}"
        editTextFileName.setText(defaultFileName)
        editTextFileName.selectAll() // 전체 선택하여 쉽게 수정할 수 있도록
        
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
            // 이미지 컨테이너 (빌라저와 의류가 겹쳐진 부분) 스크린샷
            val imageContainer = view?.findViewById<FrameLayout>(R.id.imageContainer)
            imageContainer?.let { container ->
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

        // 어댑터 데이터만 업데이트 (재생성하지 않음)
        adapter.updateData(filteredGroups)
        updateButtonStyles(currentFilter)
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
    
    // 빌라저 설정
    private fun setupVillager() {
        currentVillager = Villager(
            name = "Joey",
            imagePath = "images/villagers/Joey.png",
            hatPosition = ClothingPosition(
                x = 0.5f,
                y = 0.27f,
                scaleX = 3.0f,
                scaleY = 3.0f,
                rotation = 0f
            ),
            topPosition = ClothingPosition(
                x = 0.5f,
                y = 0.60f,
                scaleX = 1.1f,
                scaleY = 0.9f,
                rotation = 0f
            ),
            bottomPosition = ClothingPosition(
                x = 0.5f,
                y = 0.74f,
                scaleX = 0.9f,
                scaleY = 0.5f,
                rotation = 0f
            ),
            shoesPosition = ClothingPosition(
                x = 0.5f,
                y = 0.82f,
                scaleX = 0.5f,
                scaleY = 0.3f,
                rotation = 0f
            )
        )
    }
    
    private fun setupOverlappingImages() {
        // 빌라저 기본 이미지 로드
        loadImageFromAssets("images/villagers/Joey.png", imageVillager)
        
        // 기본 아이템들을 선택된 아이템으로 설정
        val topGroup = globalItemGroups.find { it.tag == ItemTag.TOP }
        val bottomGroup = globalItemGroups.find { it.tag == ItemTag.BOTTOM }
        val hatGroup = globalItemGroups.find { it.tag == ItemTag.HAT }
        val shoesGroup = globalItemGroups.find { it.tag == ItemTag.SHOES }
        
        selectedTop = topGroup?.items?.firstOrNull()
        selectedBottom = bottomGroup?.items?.firstOrNull()
        selectedHat = hatGroup?.items?.firstOrNull()
        selectedShoes = shoesGroup?.items?.firstOrNull()
        
        selectedTopGroup = topGroup
        selectedBottomGroup = bottomGroup
        selectedHatGroup = hatGroup
        selectedShoesGroup = shoesGroup
        
        // 선택된 아이템들로 이미지 로드
        selectedTop?.let { 
            loadImageFromAssets(it.imagePath, imageTop)
            loadImageFromAssets(it.imagePath, equippedTopImage)
        }
        selectedBottom?.let { 
            loadImageFromAssets(it.imagePath, imageBottom)
            loadImageFromAssets(it.imagePath, equippedBottomImage)
        }
        selectedHat?.let { 
            loadImageFromAssets(it.imagePath, imageHat)
            loadImageFromAssets(it.imagePath, equippedHatImage)
        }
        selectedShoes?.let { 
            loadImageFromAssets(it.imagePath, imageShoes)
            loadImageFromAssets(it.imagePath, equippedShoesImage)
        }
    }
    
    // 이미지 스타일 설정
    private fun setupImageStyles() {
        currentVillager?.let { villager ->
            // 빌라저 기본 이미지 (맨 아래) - 더 큰 크기로 설정
            setupImageStyle(imageVillager, 200, 200, 0, 0, android.R.color.transparent)
            
            // 신발 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageShoes, villager.shoesPosition, 120, 120)
            
            // 하의 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageBottom, villager.bottomPosition, 100, 100)
            
            // 상의 이미지 - villager 위치 정보 사용
            setupClothingImageStyle(imageTop, villager.topPosition, 80, 80)
            
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
        
        // 스케일 타입 설정 - 빌라저는 FIT_CENTER로 설정하여 잘리지 않도록
        if (imageView == imageVillager) {
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
    
    // 의류 이미지 스타일 설정 (ClothingPosition 사용)
    private fun setupClothingImageStyle(
        imageView: ImageView,
        position: ClothingPosition,
        baseWidth: Int,
        baseHeight: Int
    ) {
        val scaledWidth = (baseWidth * position.scaleX * resources.displayMetrics.density).toInt()
        val scaledHeight = (baseHeight * position.scaleY * resources.displayMetrics.density).toInt()

        val layoutParams = imageView.layoutParams as FrameLayout.LayoutParams
        layoutParams.width = scaledWidth
        layoutParams.height = scaledHeight

        // 중심 위치로 이동 (부모 FrameLayout 기준)
        val parent = imageView.parent as View
        val parentWidth = parent.width
        val parentHeight = parent.height
        if (parentWidth > 0 && parentHeight > 0) {
            layoutParams.leftMargin = (parentWidth * position.x - scaledWidth / 2).toInt()
            layoutParams.topMargin = (parentHeight * position.y - scaledHeight / 2).toInt()
        }

        layoutParams.gravity = android.view.Gravity.TOP or android.view.Gravity.START
        imageView.layoutParams = layoutParams

        // 스케일, 회전 적용
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.rotation = position.rotation
        imageView.setPadding(0, 0, 0, 0)
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
        when (group.tag) {
            ItemTag.HAT -> {
                selectedHat = item
                selectedHatGroup = group
                loadImageFromAssets(item.imagePath, imageHat)
                loadImageFromAssets(item.imagePath, equippedHatImage)
            }
            ItemTag.TOP -> {
                selectedTop = item
                selectedTopGroup = group
                loadImageFromAssets(item.imagePath, imageTop)
                loadImageFromAssets(item.imagePath, equippedTopImage)
            }
            ItemTag.BOTTOM -> {
                selectedBottom = item
                selectedBottomGroup = group
                loadImageFromAssets(item.imagePath, imageBottom)
                loadImageFromAssets(item.imagePath, equippedBottomImage)
            }
            ItemTag.SHOES -> {
                selectedShoes = item
                selectedShoesGroup = group
                loadImageFromAssets(item.imagePath, imageShoes)
                loadImageFromAssets(item.imagePath, equippedShoesImage)
            }
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