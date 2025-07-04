package com.example.nookstyle.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.*
import com.example.nookstyle.ui.adapter.ItemGroupAdapter
import java.io.IOException



class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemGroupAdapter

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

    private var allItemGroups = listOf<ItemGroup>()
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
        
        // RecyclerView 설정
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        setupData()
        setupTagButtons()
        setupVillager()
        setupOverlappingImages()
        view.post { setupImageStyles() }
    }

    // ItemGroup 데이터 세팅
    private fun setupData() {
        allItemGroups = listOf(
            ItemGroup(
                title = "개구리 모자",
                tag = ItemTag.HAT,
                items = listOf(
                    Item("개구리 모자", ItemTag.HAT, "초록", "1120벨", "280 마일", "images/cloths/hat/froghat/1_green.webp"),
                    Item("개구리 모자", ItemTag.HAT, "파랑", "1120벨", "280 마일", "images/cloths/hat/froghat/2_blue.webp"),
                    Item("개구리 모자", ItemTag.HAT, "빨강", "1120벨", "280 마일", "images/cloths/hat/froghat/3_red.webp"),
                    Item("개구리 모자", ItemTag.HAT, "노랑", "1120벨", "280 마일", "images/cloths/hat/froghat/4_yellow.webp")
                )
            )
            // ➡️ 여기에 ItemGroup 추가하면 자동으로 RecyclerView에 표시됨
        )

        adapter = ItemGroupAdapter(allItemGroups)
        recyclerView.adapter = adapter
    }
    
    // 태그 버튼 설정
    private fun setupTagButtons() {
        btnAll.setOnClickListener { filterItems(null) }
        btnTop.setOnClickListener { filterItems(ItemTag.TOP) }
        btnBottom.setOnClickListener { filterItems(ItemTag.BOTTOM) }
        btnHat.setOnClickListener { filterItems(ItemTag.HAT) }
        btnShoes.setOnClickListener { filterItems(ItemTag.SHOES) }
    }
    
    // 아이템 필터링
    private fun filterItems(tag: ItemTag?) {
        val filtered = if (tag == null) {
            allItemGroups // 전체 보기
        } else {
            allItemGroups.filter { it.tag == tag } // 특정 태그만 필터링
        }
        adapter = ItemGroupAdapter(filtered)
        recyclerView.adapter = adapter
        
        // 버튼 스타일 업데이트
        updateButtonStyles(tag)
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
} 