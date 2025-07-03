package com.example.nookstyle.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Item
import com.example.nookstyle.ui.adapter.ItemAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        recyclerView = findViewById(R.id.recyclerView)
        
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
    }
}
