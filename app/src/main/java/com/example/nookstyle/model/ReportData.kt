package com.example.nookstyle.model

import java.util.Date

data class ReportData(
    val imageName: String,
    val reportReason: String,
    val otherReason: String? = null,
    val reportDate: Date = Date(),
    val isProcessed: Boolean = false
)

enum class ReportReason(val displayName: String) {
    INAPPROPRIATE("부적절한 콘텐츠"),
    COPYRIGHT("저작권 침해"),
    SPAM("스팸 또는 광고"),
    VIOLENCE("폭력적이거나 위험한 콘텐츠"),
    OTHER("기타")
} 