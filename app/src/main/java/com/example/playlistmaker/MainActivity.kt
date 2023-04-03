package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.poster)
        image.setOnClickListener(this@MainActivity)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.poster -> {
                Toast.makeText(this, "Нажали на картинку!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}