package com.example.nookstyle.ui.main

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.example.nookstyle.R

class ItemButtonActivity : AppCompatActivity() {
    private lateinit var imageViewItem: ImageView
    private lateinit var buttonPrev: ImageButton
    private lateinit var buttonNext: ImageButton

    private val category = "hat"
    private val itemName = "froghat"
    private val folderPath = "images/cloths/$category/$itemName"

    private var imageFiles: Array<String>? = null
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_button_activity)

        imageViewItem = findViewById(R.id.imageViewItem)
        buttonPrev = findViewById(R.id.buttonPrev)
        buttonNext = findViewById(R.id.buttonNext)

        val assetManager = assets
        try {
            imageFiles = assetManager.list(folderPath)
            imageFiles?.sort()
            if (!imageFiles.isNullOrEmpty()) {
                loadImage("$folderPath/${imageFiles!![currentIndex]}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        buttonNext.setOnClickListener {
            imageFiles?.let {
                currentIndex = (currentIndex + 1) % it.size
                loadImage("$folderPath/${it[currentIndex]}")
            }
        }

        buttonPrev.setOnClickListener {
            imageFiles?.let {
                currentIndex = (currentIndex - 1 + it.size) % it.size
                loadImage("$folderPath/${it[currentIndex]}")
            }
        }
    }

    private fun loadImage(assetPath: String) {
        try {
            val inputStream = assets.open(assetPath)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageViewItem.setImageBitmap(bitmap)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}