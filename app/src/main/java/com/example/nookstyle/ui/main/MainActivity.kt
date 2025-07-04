package com.example.nookstyle.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nookstyle.R
import com.example.nookstyle.ui.fragments.Tab1Fragment
import com.example.nookstyle.ui.fragments.Tab2Fragment
import com.example.nookstyle.ui.fragments.Tab3Fragment
import android.content.Intent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var tab1: TextView
    private lateinit var tab2: TextView
    private lateinit var tab3: TextView
    private var currentSelectedTab: TextView? = null
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 시스템 UI 설정 - 상태바는 유지하되 전체화면 모드 사용
        setupSystemUI()
        
        // 권한 확인 및 요청
        checkAndRequestPermissions()
        
        // 뷰 초기화
        tab1 = findViewById(R.id.tab1)
        tab2 = findViewById(R.id.tab2)
        tab3 = findViewById(R.id.tab3)

        // 탭 클릭 이벤트 설정
        setupTabClickListeners()
        
        // 기본적으로 첫 번째 탭 표시
        showTab1()
    }
    
    private fun setupSystemUI() {
        // 상태바는 유지하되 시스템 UI와 겹치지 않도록 설정
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )
        
        // Android 11+ (API 30+) 에서는 WindowInsetsController 사용
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        
        val permissionsToRequest = mutableListOf<String>()
        
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // 권한이 승인됨
                } else {
                    // 권한이 거부됨
                }
            }
        }
    }

    private fun setupTabClickListeners() {
        tab1.setOnClickListener { if (currentSelectedTab != tab1) showTab1() }
        tab2.setOnClickListener { if (currentSelectedTab != tab2) showTab2() }
        tab3.setOnClickListener { if (currentSelectedTab != tab3) showTab3() }
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

    private fun updateTabSelection(selectedTab: TextView, vararg otherTabs: TextView) {
        // 선택된 탭 활성화 (selector drawable의 state_selected 사용)
        selectedTab.isSelected = true
        otherTabs.forEach { it.isSelected = false }
        currentSelectedTab = selectedTab
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentArea, fragment)
            .commit()
    }
}
