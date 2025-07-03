package com.example.nookstyle.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nookstyle.R
import com.example.nookstyle.model.Item
import com.example.nookstyle.ui.adapter.ItemAdapter

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

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
            Item("제목 10", "설명 10")
        )

        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter
    }
} 