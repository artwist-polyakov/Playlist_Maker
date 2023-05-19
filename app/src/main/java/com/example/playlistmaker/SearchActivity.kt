package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapters.TrackAdapter
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.tracks
import com.example.playlistmaker.networkClient.ITunesApi
import com.example.playlistmaker.networkClient.SongsSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var problemsLayout: LinearLayout
    private lateinit var problemsText: TextView
    private lateinit var problemsIcon: ImageView
    private val tracks = ArrayList<Track>()
    private lateinit var refreshButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var trackAdapter: TrackAdapter
    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
        const val API_URL = "https://itunes.apple.com"
        const val NO_INTERNET_CONNECTION = """Проблемы со связью

Загрузка не удалась. Проверьте подключение к интернету
"""
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(SearchActivity.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<ImageView>(R.id.return_button)
        recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(tracks)
        recyclerView.adapter = trackAdapter
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearIcon)
        problemsLayout = findViewById(R.id.problems_layout)
        problemsText = findViewById(R.id.search_placeholder_text)
        problemsIcon = findViewById(R.id.problems_image)
        refreshButton = findViewById(R.id.refresh_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    search(searchEditText.text.toString())
                }
            }
            false
        }

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
            searchEditText.setText(savedInstanceState.getString(SEARCH_QUERY, ""))
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
            hideProblemsLayout()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

//         прослушиватель нажатия на кнопку "назад"
        backButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("searchQuery", searchEditText.text.toString())
            editor.apply()
            this.finish()
        }
        refreshButton.setOnClickListener {
            search(searchEditText.text.toString())
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

    private fun search(searchQuery: String) {
        tracks.clear()
        trackAdapter.notifyDataSetChanged()
        loadingIndicator.visibility = View.VISIBLE
        hideProblemsLayout()
        recyclerView.visibility = View.GONE
        val call = itunesService.search(searchQuery)
        call.enqueue(object : Callback<SongsSearchResponse> {
            override fun onResponse(
                call: Call<SongsSearchResponse>,
                response: Response<SongsSearchResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            loadingIndicator.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            tracks.addAll(response.body()?.results!!)
                        } else {
                            showProblemsLayout("nothing_found")
                        }
                    }

                    else -> {
                        showProblemsLayout("error")
                    }
                }
            }

            override fun onFailure(call: Call<SongsSearchResponse>, t: Throwable) {
                showProblemsLayout()
            }
        })
    }

    private fun showProblemsLayout(state: String = "error") {
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.VISIBLE
        when (state) {
            "error" -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = SearchActivity.NO_INTERNET_CONNECTION
                problemsIcon.setImageResource(R.drawable.no_internet)
                refreshButton.visibility = View.VISIBLE
                refreshButton.setOnClickListener {
                    search(searchEditText.text.toString())
                }
            }

            "nothing_found" -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = getString(R.string.nothing_found)
                problemsIcon.setImageResource(R.drawable.nothing_found)
                refreshButton.visibility = View.GONE
            }
        }
    }

    private fun hideProblemsLayout() {
        recyclerView.visibility = View.VISIBLE
        problemsLayout.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

}