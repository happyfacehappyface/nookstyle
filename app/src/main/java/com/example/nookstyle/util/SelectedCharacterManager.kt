package com.example.nookstyle.util

import com.example.nookstyle.model.Villager

object SelectedCharacterManager {
    private var selectedVillager: Villager? = null

    fun setSelectedVillager(villager: Villager) {
        selectedVillager = villager
    }

    fun getSelectedVillager(): Villager? {
        return selectedVillager
    }

    fun clearSelectedVillager() {
        selectedVillager = null
    }
}