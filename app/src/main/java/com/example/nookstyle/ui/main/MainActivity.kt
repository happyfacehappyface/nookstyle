package com.example.nookstyle.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Item
import com.example.nookstyle.ui.adapter.ItemAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var tab1: Button
    private lateinit var tab2: Button
    private lateinit var tab3: Button
    private lateinit var tab2Content: View
    private lateinit var tab3Content: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 뷰 초기화
        recyclerView = findViewById(R.id.recyclerView)
        tab1 = findViewById(R.id.tab1)
        tab2 = findViewById(R.id.tab2)
        tab3 = findViewById(R.id.tab3)
        tab2Content = findViewById(R.id.tab2Content)
        tab3Content = findViewById(R.id.tab3Content)
        
        // GridLayoutManager 사용 - 2열로 설정
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // 더 많은 아이템 추가
        val itemList = listOf(
            Item("제목 1", "설명 1"),
            Item("제목 2", "설명 2"),
            Item("제목 3", "설명 3"),
            Item("제목 4", "설명 4"),
            Item("제목 5", "설명 5"),
            Item("제목 6", "설명 6"),
            Item("제목 7", "설명 7"),
            Item("제목 8", "설명 8"),
            Item("제목 9", "설명 9"),
            Item("제목 10", "설명 10"),
            Item("제목 11", "설명 11"),
            Item("제목 12", "설명 12"),
            Item("제목 13", "설명 13"),
            Item("제목 14", "설명 14"),
            Item("제목 15", "설명 15"),
            Item("제목 16", "설명 16"),
            Item("제목 17", "설명 17"),
        )

        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter

        // 탭 클릭 이벤트 설정
        setupTabClickListeners()
    }

    private fun setupTabClickListeners() {
        tab1.setOnClickListener {
            showTab1()
        }
        
        tab2.setOnClickListener {
            showTab2()
        }
        
        tab3.setOnClickListener {
            showTab3()
        }
    }

    private fun showTab1() {
        // 탭 1 활성화
        tab1.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        tab1.setTextColor(getColor(android.R.color.white))
        
        tab2.setBackgroundColor(getColor(android.R.color.white))
        tab2.setTextColor(getColor(android.R.color.black))
        
        tab3.setBackgroundColor(getColor(android.R.color.white))
        tab3.setTextColor(getColor(android.R.color.black))

        // 콘텐츠 전환
        recyclerView.visibility = View.VISIBLE
        tab2Content.visibility = View.GONE
        tab3Content.visibility = View.GONE
    }

    private fun showTab2() {
        // 탭 2 활성화
        tab1.setBackgroundColor(getColor(android.R.color.white))
        tab1.setTextColor(getColor(android.R.color.black))
        
        tab2.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        tab2.setTextColor(getColor(android.R.color.white))
        
        tab3.setBackgroundColor(getColor(android.R.color.white))
        tab3.setTextColor(getColor(android.R.color.black))

        // 콘텐츠 전환
        recyclerView.visibility = View.GONE
        tab2Content.visibility = View.VISIBLE
        tab3Content.visibility = View.GONE
    }

    private fun showTab3() {
        // 탭 3 활성화
        tab1.setBackgroundColor(getColor(android.R.color.white))
        tab1.setTextColor(getColor(android.R.color.black))
        
        tab2.setBackgroundColor(getColor(android.R.color.white))
        tab2.setTextColor(getColor(android.R.color.black))
        
        tab3.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        tab3.setTextColor(getColor(android.R.color.white))

        // 콘텐츠 전환
        recyclerView.visibility = View.GONE
        tab2Content.visibility = View.GONE
        tab3Content.visibility = View.VISIBLE
    }
}
