package com.example.nookstyle.model

val globalItemGroups = mutableListOf<ItemGroup>()

fun createItem(
    title: String,
    tag: ItemTag,
    color: String,
    price_bell: String,
    price_mile: String,
    imagePath: String
) {
    val item = Item(
        color = color,
        imagePath = imagePath
    )

    val existingGroup = globalItemGroups.find { it.title == title && it.tag == tag }

    if (existingGroup != null) {
        val newItems = existingGroup.items.toMutableList()
        newItems.add(item)
        globalItemGroups[globalItemGroups.indexOf(existingGroup)] =
            existingGroup.copy(items = newItems)
    } else {
        val newGroup = ItemGroup(
            title = title,
            tag = tag,
            price_bell = price_bell,
            price_mile = price_mile,
            items = listOf(item)
        )
        globalItemGroups.add(newGroup)
    }
}