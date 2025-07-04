package com.example.nookstyle.model

data class Item(
    val title: String,
    val description: String,
    val tag: ItemTag, // enum으로 정의된 태그
    val imagePath: String // 이미지 파일 경로
)