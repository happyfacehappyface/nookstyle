package com.example.nookstyle.model

data class ItemGroup (
    val title: String,
    val tag: ItemTag,
    val items: List<Item>
)