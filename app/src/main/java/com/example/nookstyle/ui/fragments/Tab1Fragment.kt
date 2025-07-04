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
import com.example.nookstyle.model.Item
import com.example.nookstyle.model.ItemTag
import com.example.nookstyle.ui.adapter.ItemAdapter
import java.io.IOException

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    
    // 태그 버튼들
    private lateinit var btnAll: Button
    private lateinit var btnTop: Button
    private lateinit var btnBottom: Button
    private lateinit var btnHat: Button
    private lateinit var btnShoes: Button
    
    // 전체 아이템 리스트 (필터링용)
    private var allItems = listOf<Item>()
    private var currentFilter: ItemTag? = null

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
        image1 = view.findViewById(R.id.image1)
        image2 = view.findViewById(R.id.image2)
        image3 = view.findViewById(R.id.image3)
        
        // 태그 버튼 초기화
        btnAll = view.findViewById(R.id.btnAll)
        btnTop = view.findViewById(R.id.btnTop)
        btnBottom = view.findViewById(R.id.btnBottom)
        btnHat = view.findViewById(R.id.btnHat)
        btnShoes = view.findViewById(R.id.btnShoes)
        
        // RecyclerView 설정
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // 전체 아이템 리스트 생성
        allItems = listOf(
            Item("제목 1", "설명 1", ItemTag.TOP, "images/cloths/hat/froghat/1_green.webp"),
            Item("제목 2", "설명 2", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("제목 3", "설명 3", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("제목 4", "설명 4", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("제목 5", "설명 5", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("제목 6", "설명 6", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("제목 7", "설명 7", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("제목 8", "설명 8", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp"),
            Item("제목 9", "설명 9", ItemTag.TOP, "images/cloths/top/TopsTexTopTshirtsHNumberball2.webp"),
            Item("제목 10", "설명 10", ItemTag.BOTTOM, "images/cloths/bottom/BottomsTexPantsNormalLeather0.webp")
        )

        adapter = ItemAdapter(allItems)
        recyclerView.adapter = adapter
        
        // 태그 버튼 클릭 리스너 설정
        setupTagButtons()
        
        // 겹쳐진 이미지 설정
        setupOverlappingImages()
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
        currentFilter = tag
        
        val filteredItems = if (tag == null) {
            allItems // 전체 보기
        } else {
            allItems.filter { it.tag == tag } // 특정 태그만 필터링
        }
        
        adapter = ItemAdapter(filteredItems)
        recyclerView.adapter = adapter
        
        // 버튼 스타일 업데이트
        updateButtonStyles(tag)
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
    
    private fun setupOverlappingImages() {
        // 방법 1: res/drawable에서 불러오기 (권장)
        // 이미지 파일을 res/drawable 폴더에 넣고 사용
        // image1.setImageResource(R.drawable.my_image1)
        // image2.setImageResource(R.drawable.my_image2)
        // image3.setImageResource(R.drawable.my_image3)

        loadImageFromAssets("images/villagers/Joey.png", image1)
        loadImageFromAssets("images/cloths/top/TopsTexTopTshirtsHNumberball2.webp", image2)
        loadImageFromAssets("images/cloths/bottom/BottomsTexPantsNormalLeather0.webp", image3)
        
        // 이미지 스타일 설정
        setupImageStyles()

        /*
        // 현재는 시스템 아이콘 사용
        image1.setImageResource(android.R.drawable.ic_menu_gallery)
        image1.scaleType = ImageView.ScaleType.CENTER_CROP
        
        image2.setImageResource(android.R.drawable.ic_menu_camera)
        image2.scaleType = ImageView.ScaleType.CENTER_CROP
        
        image3.setImageResource(android.R.drawable.ic_menu_view)
        image3.scaleType = ImageView.ScaleType.CENTER_CROP
        */
        
        // 다른 시스템 이미지 옵션들:
        // image1.setImageResource(android.R.drawable.ic_menu_compass)
        // image2.setImageResource(android.R.drawable.ic_menu_help)
        // image3.setImageResource(android.R.drawable.ic_menu_info_details)
    }
    
    // 이미지 스타일 설정
    private fun setupImageStyles() {
        // 이미지 1 스타일 설정 (투명 배경)
        setupImageStyle(image1, 140, 140, 0, 0, android.R.color.transparent)
        
        // 이미지 2 스타일 설정 (투명 배경)
        setupImageStyle(image2, 110, 110, 25, 25, android.R.color.transparent)
        
        // 이미지 3 스타일 설정 (투명 배경)
        setupImageStyle(image3, 80, 80, 50, 50, android.R.color.transparent)
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
        
        // 스케일 타입 설정
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        
        // 패딩 설정
        val padding = (8 * resources.displayMetrics.density).toInt()
        imageView.setPadding(padding, padding, padding, padding)
        
        // 레이아웃 파라미터 적용
        imageView.layoutParams = layoutParams
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