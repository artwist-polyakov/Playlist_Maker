package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
            val backButton = findViewById<ImageView>(R.id.return_button)
        val sharingLayout = findViewById<LinearLayout>(R.id.sharing_layout)
        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        val agreementLayout = findViewById<LinearLayout>(R.id.agreement_layout)
        val sharingAction = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "https://practicum.yandex.ru/android-developer/")
            startActivity(intent)
        }
        val supportAction = {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:"))
            intent.putExtra(Intent.EXTRA_EMAIL, "master@artwist.ru")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
            intent.putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")
            startActivity(intent)
        }

        val agreementAction = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://yandex.ru/legal/practicum_offer/"))
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

        supportLayout.setOnClickListener {
            supportAction()
        }

        agreementLayout.setOnClickListener {
            agreementAction()
        }
    }

    }
