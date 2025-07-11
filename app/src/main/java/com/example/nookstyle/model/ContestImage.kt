package com.example.nookstyle.model

import java.io.File

data class ContestImage(
    val imageName: String,
    var likeCount: Int = 0,
    var isLiked: Boolean = false,
    val file: File? = null,
    val isSubmitted: Boolean = false // 출품된 작품인지 여부
) 