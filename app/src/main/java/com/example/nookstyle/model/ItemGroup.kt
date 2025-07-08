package com.example.nookstyle.model

data class ItemGroup (
    val title: String,
    val tag: ItemTag,
    val price_bell: String,
    val price_mile: String,
    val items: List<Item>,
    val x: Float = 0f,
    val y: Float = 0f,
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val rotation: Float = 0.0f
)