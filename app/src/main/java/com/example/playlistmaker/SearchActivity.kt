package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapters.TrackAdapter
import com.example.playlistmaker.history.LinkedRepository
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.networkClient.ITunesApi
import com.example.playlistmaker.networkClient.SongsSearchResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

typealias TrackList = ArrayList<Track>

enum class ResponseState{
    SUCCESS,
    NOTHING_FOUND,
    ERROR
}

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var problemsLayout: LinearLayout
    private lateinit var problemsText: TextView
    private lateinit var problemsIcon: ImageView
    private lateinit var refreshButton: Button
    private lateinit var cleanHistoryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var linkedRepository: LinkedRepository<Track>
    private lateinit var handler: Handler
    private lateinit var searchRunnable: Runnable


    companion object {
        const val SEARCH_QUERY = "SEARCH_QUERY"
        const val TRACKS = "TRACKS"
        const val API_URL = "https://itunes.apple.com"
        const val PREFS = "my_prefs"
        const val QUERY = "searchQuery"
        const val TRACKS_LIST = "TRACKS_LIST"
        const val RESPONSE_STATE = "responseState"
        const val MAX_HISTORY_SIZE = 10
        const val HISTORY = "history"
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
    }

    // NETWORK
    private val retrofit = Retrofit.Builder()
        .baseUrl(SearchActivity.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ITunesApi::class.java)

    // TODO сделать методы onPause и onResume для сохранения состояния приложения
    /*
    Пока методы не делаем, потому что надо как то переделать работу с активити и реализовать ее
    презентер что ли. Так как в текущей версии, надо будет тиражировать код, что не есть хорошо.
    */
    override fun onPause() {
        super.onPause()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onStop() {
        super.onStop()
        linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onResume() {
        super.onResume()
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val backButton = findViewById<ImageView>(R.id.return_button)

        // SEARCH RECYCLER VIEW
        recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // SEARCH
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearIcon)
        problemsLayout = findViewById(R.id.problems_layout)
        problemsText = findViewById(R.id.search_placeholder_text)
        problemsIcon = findViewById(R.id.problems_image)
        refreshButton = findViewById(R.id.refresh_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        cleanHistoryButton = findViewById(R.id.clear_button)
        handler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { search() }

        // HISTORY VIEW
        historyLayout = findViewById(R.id.history_layout)
        hideHistoryLayout()
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        linkedRepository = LinkedRepository(MAX_HISTORY_SIZE)
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)

        // ADAPTERS
        historyAdapter = TrackAdapter(linkedRepository)
        trackAdapter = TrackAdapter(linkedRepository)
        recyclerView.adapter = trackAdapter
        historyRecyclerView.adapter = historyAdapter

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    search()
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
                    searchDebounce()
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

        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val searchQuery = sharedPref.getString(QUERY, "")
        val json = sharedPref.getString(TRACKS_LIST, ResponseState.SUCCESS.name)
        val state = ResponseState.valueOf(
            sharedPref.getString(RESPONSE_STATE, ResponseState.SUCCESS.name)
                ?: ResponseState.SUCCESS.name
        )

        if (searchQuery.isNullOrEmpty()) {
            makeClearButtonInvisible()
        } else {
            makeClearButtonVisible()
        }
        searchEditText.setText(searchQuery)

        if (json.isNullOrEmpty()) {
            hideProblemsLayout()
        } else {
            if (searchEditText.text.isNullOrEmpty()) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
            val gson = Gson()
            val type = object : TypeToken<TrackList>() {}.type
            if (json.startsWith("[")) {
                val restoredTracks: TrackList = gson.fromJson(json, type)
                trackAdapter.setTracks(restoredTracks)
                hideProblemsLayout()
            } else {
                // JSON-строка не является массивом
                showProblemsLayout(responseState = ResponseState.NOTHING_FOUND)
            }
        }

        if (state != ResponseState.SUCCESS) {
            showProblemsLayout(state)
        } else {
            hideProblemsLayout()
        }

        searchEditText.addTextChangedListener(simpleTextWatcher)

        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if ((hasFocus) && (searchEditText.text.isEmpty()) && (linkedRepository.getSize() > 0)) {
                showHistoryLayout()
            } else {
                hideHistoryLayout()
            }
        }

        // прослушиватель нажатия на кнопку "очистить историю"
        cleanHistoryButton.setOnClickListener {
            linkedRepository.clear()
            linkedRepository.clearSharedPreferences(PREFS, HISTORY, this)
            hideHistoryLayout()
        }

        // прослушиватель нажатия на кнопку "очистить"
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(searchEditText.windowToken, 0) // скрыть клавиатуру
//            searchEditText.clearFocus()
            trackAdapter.setTracks(null)
            val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(RESPONSE_STATE, ResponseState.SUCCESS.name)
            editor.apply()
            hideProblemsLayout()
            searchEditText.requestFocus()
            showHistoryLayout()
        }

//         прослушиватель нажатия на кнопку "назад"
        backButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(QUERY, searchEditText.text.toString())
            val gson = Gson()
            val json = gson.toJson(trackAdapter.getTracks())
            editor.putString(TRACKS_LIST, json)
            editor.apply()
            linkedRepository.saveToSharedPreferences(PREFS, HISTORY, this)
            this.finish()
        }
        refreshButton.setOnClickListener {
            search()
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
        // TODO сохранять еще и errorState
        outState.putString(SEARCH_QUERY, searchEditText.text.toString())
        outState.putParcelableArrayList(TRACKS, trackAdapter.getTracks())
        outState.putParcelableArrayList(HISTORY, historyAdapter.getTracks())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // TODO восстанавливать еще и errorState
        // Вторым параметром мы передаём значение по умолчанию
        val searchQuery = savedInstanceState.getString(SEARCH_QUERY, "")
        searchEditText.setText(searchQuery)
        linkedRepository.restoreFromSharedPreferences(PREFS, HISTORY, this)
        historyAdapter = TrackAdapter(linkedRepository)
        trackAdapter = TrackAdapter(linkedRepository)
        // TODO когда метод getParcelableArrayList уберут, надо будет использвать сериализацию в json и восстоновление из json
        val restoredTracks = savedInstanceState.getParcelableArrayList<Track>(TRACKS)
        if (restoredTracks != null) {
            trackAdapter.setTracks(restoredTracks)
        }
        val restoredHistory = savedInstanceState.getParcelableArrayList<Track>(HISTORY)
        if (restoredHistory != null) {
            historyAdapter.setTracks(restoredHistory)
        }
        historyRecyclerView.adapter = historyAdapter
        recyclerView.adapter = trackAdapter
    }

    private fun search() {
        val searchQuery = searchEditText.text.toString()
        trackAdapter.setTracks(null)
        loadingIndicator.visibility = View.VISIBLE
        hideHistoryLayout()
        hideProblemsLayout()
        recyclerView.visibility = View.GONE
        val call = itunesService.search(searchQuery)
        call.enqueue(object : Callback<SongsSearchResponse> {
            override fun onResponse(
                call: Call<SongsSearchResponse>,
                response: Response<SongsSearchResponse>
            ) {
                val responseState = when {
                    response.isSuccessful && response.body()?.results?.isNotEmpty() == true -> ResponseState.SUCCESS
                    response.isSuccessful -> ResponseState.NOTHING_FOUND
                    else -> ResponseState.ERROR
                }
                val sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(RESPONSE_STATE, responseState.name)
                editor.apply()
                when (responseState) {
                    ResponseState.SUCCESS -> {
                        hideHistoryLayout()
                        loadingIndicator.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        val sharedPreferences =
                            getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString(TRACKS_LIST, response.body()?.results!!.toString())
                        trackAdapter.setTracks(response.body()?.results!!)
                    }

                    ResponseState.NOTHING_FOUND -> showProblemsLayout(responseState)
                    ResponseState.ERROR -> showProblemsLayout(responseState)
                }
            }

            override fun onFailure(call: Call<SongsSearchResponse>, t: Throwable) {
                val responseState = ResponseState.ERROR
                showProblemsLayout(responseState)
            }
        })
    }


    private fun showProblemsLayout(responseState: ResponseState) {
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.VISIBLE
        when (responseState.name) {
            ResponseState.ERROR.name -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = getString(R.string.no_internet)
                problemsIcon.setImageResource(R.drawable.no_internet)
                refreshButton.visibility = View.VISIBLE
                refreshButton.setOnClickListener {
                    search()
                }
            }

            ResponseState.NOTHING_FOUND.name -> {
                recyclerView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                problemsText.text = getString(R.string.nothing_found)
                problemsIcon.setImageResource(R.drawable.nothing_found)
                refreshButton.visibility = View.GONE
            }
        }
    }
    private fun searchDebounce() {

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)


    }
    private fun hideProblemsLayout() {
        recyclerView.visibility = View.VISIBLE
        problemsLayout.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showHistoryLayout() {
        historyLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.GONE
        cleanHistoryButton.visibility = View.VISIBLE
        val gson = Gson()
        val json = gson.toJson(linkedRepository.get(reversed = true))
        val type = object : TypeToken<TrackList>() {}.type
        val restoredTracks: TrackList = gson.fromJson(json, type)
        historyAdapter.setTracks(restoredTracks)
    }

    private fun hideHistoryLayout() {
        recyclerView.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        cleanHistoryButton.visibility = View.VISIBLE


    }





}