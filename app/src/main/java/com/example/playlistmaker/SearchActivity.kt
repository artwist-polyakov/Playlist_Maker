package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<ImageView>(R.id.return_button)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                    searchEditText.background = getDrawable(R.drawable.rounded_edittext)
                } else {
                    clearButton.visibility = View.VISIBLE
                    clearButton.background = getDrawable(R.drawable.right_rounded_edittext)
                    searchEditText.background = getDrawable(R.drawable.left_rounded_edittext)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // тут надо будет совершить поиск
            }
        }


        searchEditText.addTextChangedListener(simpleTextWatcher)

        // прослушиватель нажатия на кнопку "очистить"
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0) // скрыть клавиатуру
            searchEditText.clearFocus()


        }

        // прослушиватель нажатия на кнопку "назад"
        backButton.setOnClickListener {
            this.finish()
        }
        
    }
}