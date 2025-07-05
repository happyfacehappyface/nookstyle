package com.example.nookstyle.model

data class ItemGroup (
    val title: String,
    val tag: ItemTag,
    val price_bell: String,
    val price_mile: String,
    val items: List<Item>
)