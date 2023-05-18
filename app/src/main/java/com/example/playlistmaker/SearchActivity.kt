package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapters.TrackAdapter
import com.example.playlistmaker.model.tracks

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<ImageView>(R.id.return_button)
        val recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TrackAdapter(tracks)
        recyclerView.adapter = trackAdapter
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearIcon)
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    makeClearButtonInvisible()

                } else {
                    makeClearButtonVisible()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // тут надо будет совершить поиск
            }
        }
        if (savedInstanceState != null) {
            searchEditText.setText(savedInstanceState.getString(SEARCH_QUERY,""))
        }

        val sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val searchQuery = sharedPref.getString("searchQuery", "")
        if (searchQuery.isNullOrEmpty()) {
            makeClearButtonInvisible()
        } else {
            makeClearButtonVisible()
        }
        searchEditText.setText(searchQuery)

        searchEditText.addTextChangedListener(simpleTextWatcher)

        // прослушиватель нажатия на кнопку "очистить"
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0) // скрыть клавиатуру
            searchEditText.clearFocus()
        }

//         прослушиватель нажатия на кнопку "назад"
        backButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("searchQuery", searchEditText.text.toString())
            editor.apply()
            this.finish()
        }
    }

    private fun makeClearButtonInvisible() {
        clearButton.visibility = View.GONE
        searchEditText.background = getDrawable(R.drawable.rounded_edittext)
    }

    private fun makeClearButtonVisible() {
        clearButton.visibility = View.VISIBLE
        clearButton.background = getDrawable(R.drawable.right_rounded_edittext)
        searchEditText.background = getDrawable(R.drawable.left_rounded_edittext)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Вторым параметром мы передаём значение по умолчанию
        val searchQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        searchEditText.setText(searchQuery)
    }


}