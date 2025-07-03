package com.example.nookstyle.ui.main

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nookstyle.R
import com.example.nookstyle.ui.fragments.Tab1Fragment
import com.example.nookstyle.ui.fragments.Tab2Fragment
import com.example.nookstyle.ui.fragments.Tab3Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var tab1: Button
    private lateinit var tab2: Button
    private lateinit var tab3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 뷰 초기화
        tab1 = findViewById(R.id.tab1)
        tab2 = findViewById(R.id.tab2)
        tab3 = findViewById(R.id.tab3)

        // 탭 클릭 이벤트 설정
        setupTabClickListeners()
        
        // 기본적으로 첫 번째 탭 표시
        showTab1()
    }

    private fun setupTabClickListeners() {
        tab1.setOnClickListener { showTab1() }
        tab2.setOnClickListener { showTab2() }
        tab3.setOnClickListener { showTab3() }
    }

    private fun showTab1() {
        updateTabSelection(tab1, tab2, tab3)
        replaceFragment(Tab1Fragment())
    }

    private fun showTab2() {
        updateTabSelection(tab2, tab1, tab3)
        replaceFragment(Tab2Fragment())
    }

    private fun showTab3() {
        updateTabSelection(tab3, tab1, tab2)
        replaceFragment(Tab3Fragment())
    }

    private fun updateTabSelection(selectedTab: Button, vararg otherTabs: Button) {
        // 선택된 탭 활성화
        selectedTab.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        selectedTab.setTextColor(getColor(android.R.color.white))
        
        // 다른 탭들 비활성화
        otherTabs.forEach { tab ->
            tab.setBackgroundColor(getColor(android.R.color.white))
            tab.setTextColor(getColor(android.R.color.black))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentArea, fragment)
            .commit()
    }
}
