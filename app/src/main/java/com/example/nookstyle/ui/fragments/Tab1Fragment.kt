package com.example.nookstyle.ui.fragments

import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Item
import com.example.nookstyle.model.ItemTag
import com.example.nookstyle.model.Villager
import com.example.nookstyle.model.ClothingPosition
import com.example.nookstyle.ui.adapter.ItemAdapter
import com.example.nookstyle.util.ScreenshotUtil
import java.io.IOException
import android.util.Log

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var imageVillager: ImageView
    private lateinit var imageShoes: ImageView
    private lateinit var imageBottom: ImageView
    private lateinit var imageTop: ImageView
    private lateinit var imageHat: ImageView
    
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
    
    // 전체 아이템 리스트 (필터링용)
    private var allItems = listOf<Item>()
    private var currentFilter: ItemTag? = null
    private var currentSearchQuery: String = ""
    
    // 현재 빌라저
    private var currentVillager: Villager? = null

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

        // 전체 아이템 리스트 생성
        allItems = listOf(
            Item("파란 모자", "파란색 모자입니다", ItemTag.HAT, "images/cloths/hat/froghat/2_blue.webp"),
            Item("빨간 모자", "빨간색 모자입니다", ItemTag.HAT, "images/cloths/hat/froghat/3_red.webp"),
            Item("노란 모자", "노란색 모자입니다", ItemTag.HAT, "images/cloths/hat/froghat/4_yellow.webp"),
            Item("초록 모자", "초록색 모자입니다", ItemTag.HAT, "images/cloths/hat/froghat/1_green.webp"),
            Item("마리오 모자", "마리오 모자입니다", ItemTag.HAT, "images/cloths/CapHatMario0.webp"),
            Item("티셔츠", "기본 티셔츠입니다", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("가죽 바지", "가죽으로 만든 바지입니다", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("운동화", "편안한 운동화입니다", ItemTag.SHOES, "images/cloths/ShoesLowcutEggleaf0.webp"),
            Item("데님 바지", "데님 소재의 바지입니다", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("후드티", "따뜻한 후드티입니다", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("스니커즈", "스타일리시한 스니커즈입니다", ItemTag.SHOES, "images/cloths/ShoesLowcutEggleaf0.webp"),
            Item("베레모", "클래식한 베레모입니다", ItemTag.HAT, "images/cloths/hat/froghat/1_green.webp"),
            Item("폴로 셔츠", "정장용 폴로 셔츠입니다", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("청바지", "클래식한 청바지입니다", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("로퍼", "엘레간트한 로퍼입니다", ItemTag.SHOES, "images/cloths/ShoesLowcutEggleaf0.webp")
        )

        adapter = ItemAdapter(allItems)
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
        view.post {
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
        try {
            // 이미지 컨테이너 (빌라저와 의류가 겹쳐진 부분) 스크린샷
            val imageContainer = view?.findViewById<FrameLayout>(R.id.imageContainer)
            imageContainer?.let { container ->
                // 뷰가 완전히 그려진 후 스크린샷 찍기
                container.post {
                    val fileName = "Villager_Outfit_${System.currentTimeMillis()}"
                    ScreenshotUtil.captureAndSaveView(requireContext(), container, fileName)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // 아이템 필터링
    private fun filterItems(tag: ItemTag?) {
        currentFilter = tag
        applyFilters()
    }
    
    // 필터와 검색을 모두 적용
    private fun applyFilters() {
        var filteredItems = allItems
        
        // 태그 필터 적용
        if (currentFilter != null) {
            filteredItems = filteredItems.filter { it.tag == currentFilter }
        }
        
        // 검색 필터 적용
        if (currentSearchQuery.isNotEmpty()) {
            filteredItems = filteredItems.filter { 
                it.title.contains(currentSearchQuery, ignoreCase = true) 
            }
        }
        
        adapter = ItemAdapter(filteredItems)
        recyclerView.adapter = adapter
        
        // 버튼 스타일 업데이트
        updateButtonStyles(currentFilter)
    }
    
    // 버튼 스타일 업데이트
    private fun updateButtonStyles(selectedTag: ItemTag?) {
        // 모든 버튼을 기본 스타일로 초기화
        btnAll.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        btnAll.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        
        btnTop.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        btnTop.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        
        btnBottom.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        btnBottom.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        
        btnHat.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        btnHat.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        
        btnShoes.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        btnShoes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        
        // 선택된 버튼만 강조
        when (selectedTag) {
            null -> {
                btnAll.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                btnAll.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            ItemTag.TOP -> {
                btnTop.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                btnTop.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            ItemTag.BOTTOM -> {
                btnBottom.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                btnBottom.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            ItemTag.HAT -> {
                btnHat.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                btnHat.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            ItemTag.SHOES -> {
                btnShoes.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light))
                btnShoes.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
        }
    }
    
    // 빌라저 설정
    private fun setupVillager() {
        currentVillager = Villager(
            name = "Joey",
            imagePath = "images/villagers/Joey.png",
            hatPosition = ClothingPosition(
                x = 0.5f,
                y = 0.25f,
                scaleX = 2.0f,
                scaleY = 2.0f,
                rotation = 0f
            ),
            topPosition = ClothingPosition(
                x = 0.5f,
                y = 0.58f,
                scaleX = 1.0f,
                scaleY = 1.0f,
                rotation = 0f
            ),
            bottomPosition = ClothingPosition(
                x = 0.5f,
                y = 0.65f,
                scaleX = 0.8f,
                scaleY = 0.4f,
                rotation = 0f
            ),
            shoesPosition = ClothingPosition(
                x = 0.5f,
                y = 0.72f,
                scaleX = 0.4f,
                scaleY = 0.3f,
                rotation = 0f
            )
        )
    }
    
    private fun setupOverlappingImages() {
        // 빌라저 기본 이미지 로드
        loadImageFromAssets("images/villagers/Joey.png", imageVillager)
        
        // 의류 이미지들 로드
        loadImageFromAssets("images/cloths/top/TopsTexTopTshirtsHNumberball2.webp", imageTop)
        loadImageFromAssets("images/cloths/bottom/BottomsTexPantsNormalLeather0.webp", imageBottom)
        loadImageFromAssets("images/cloths/CapHatMario0.webp", imageHat)
        loadImageFromAssets("images/cloths/ShoesLowcutEggleaf0.webp", imageShoes)
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

    // 검색 기능 설정
    private fun setupSearchFunction() {
        // 한글 입력을 위한 설정
        searchEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
        
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s?.toString() ?: ""
                applyFilters()
            }
        })
    }
} 