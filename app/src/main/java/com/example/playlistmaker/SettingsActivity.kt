package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.return_button)
        val sharingLayout = findViewById<LinearLayout>(R.id.sharing_layout)
        val sharingAction = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "https://practicum.yandex.ru/android-developer/")
            startActivity(intent)
        }




        backButton.setOnClickListener {
//            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }
        sharingLayout.setOnClickListener {
            sharingAction()
        }
    }

    }
