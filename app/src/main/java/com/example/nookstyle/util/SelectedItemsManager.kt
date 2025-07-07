package com.example.nookstyle.util

import com.example.nookstyle.model.Item
import com.example.nookstyle.model.ItemGroup

object SelectedItemsManager {
    
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
    
    // 선택된 아이템 설정
    fun setSelectedHat(item: Item, group: ItemGroup) {
        selectedHat = item
        selectedHatGroup = group
    }
    
    fun setSelectedTop(item: Item, group: ItemGroup) {
        selectedTop = item
        selectedTopGroup = group
    }
    
    fun setSelectedBottom(item: Item, group: ItemGroup) {
        selectedBottom = item
        selectedBottomGroup = group
    }
    
    fun setSelectedShoes(item: Item, group: ItemGroup) {
        selectedShoes = item
        selectedShoesGroup = group
    }
    
    // 선택된 아이템 가져오기
    fun getSelectedHat(): Pair<Item?, ItemGroup?> {
        return Pair(selectedHat, selectedHatGroup)
    }
    
    fun getSelectedTop(): Pair<Item?, ItemGroup?> {
        return Pair(selectedTop, selectedTopGroup)
    }
    
    fun getSelectedBottom(): Pair<Item?, ItemGroup?> {
        return Pair(selectedBottom, selectedBottomGroup)
    }
    
    fun getSelectedShoes(): Pair<Item?, ItemGroup?> {
        return Pair(selectedShoes, selectedShoesGroup)
    }
    
    // 선택 해제
    fun clearSelectedHat() {
        selectedHat = null
        selectedHatGroup = null
    }

    fun clearSelectedTop() {
        selectedTop = null
        selectedTopGroup = null
    }

    fun clearSelectedBottom() {
        selectedBottom = null
        selectedBottomGroup = null
    }

    fun clearSelectedShoes() {
        selectedShoes = null
        selectedShoesGroup = null
    }
    
    // 선택된 아이템이 있는지 확인
    fun hasSelectedItems(): Boolean {
        return selectedHat != null || selectedTop != null || selectedBottom != null || selectedShoes != null
    }
} 