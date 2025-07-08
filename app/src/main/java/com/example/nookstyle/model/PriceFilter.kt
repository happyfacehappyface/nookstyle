package com.example.nookstyle.model

data class PriceFilter(
    val currencyType: CurrencyType,
    val minPrice: Int,
    val maxPrice: Int
)

enum class CurrencyType {
    BELLS,
    MILES
} 