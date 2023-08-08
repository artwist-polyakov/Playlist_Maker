package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.mappers.TrackToTrackDtoMapper
import com.example.playlistmaker.common.presentation.mappers.TrackToTrackInformationMappers
import com.example.playlistmaker.search.data.dto.LinkedRepository
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel

typealias TrackList = ArrayList<TrackDto>

enum class ResponseState {
    SUCCESS,
    NOTHING_FOUND,
    ERROR
}

class SearchActivity : ComponentActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private val adapter = TracksAdapter(
        object : TracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    try {
                        viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                    } catch (e: Exception) {
                        Log.e("SearchActivity", "Error saving track to history: ${e.message}", e)
                    }
                    val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    intent.putExtra("track", TrackToTrackInformationMappers().invoke(track))
                    startActivity(intent)
                }
            }
        }
    )
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewModel: SearchViewModel

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var problemsLayout: LinearLayout
    private lateinit var problemsText: TextView
    private lateinit var problemsIcon: ImageView
    private lateinit var refreshButton: Button
    private lateinit var cleanHistoryButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var linkedRepository: LinkedRepository<TrackDto>
    private lateinit var searchRunnable: Runnable
    private lateinit var textWatcher: TextWatcher
    private lateinit var backButton: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        // SEARCH RECYCLER VIEW
        recyclerView = findViewById<RecyclerView>(R.id.search_results_recycler_view)


        // SEARCH
        searchEditText = findViewById(R.id.searchEditText)
        clearButton = findViewById(R.id.clearIcon)
        problemsLayout = findViewById(R.id.problems_layout)
        problemsText = findViewById(R.id.search_placeholder_text)
        problemsIcon = findViewById(R.id.problems_image)
        refreshButton = findViewById(R.id.refresh_button)
        loadingIndicator = findViewById(R.id.loading_indicator)
        cleanHistoryButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.return_button)
        cleanHistoryButton.visibility = View.VISIBLE

        // HISTORY VIEW
        historyLayout = findViewById(R.id.history_layout)
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)


        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        historyRecyclerView.adapter = adapter
        setupListeners()
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                if (s.isNullOrEmpty()) {
                    makeClearButtonInvisible()
                    viewModel.loadHistoryTracks()
                } else {
                    makeClearButtonVisible()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        textWatcher?.let { searchEditText.addTextChangedListener(it) }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.backButtonPressed.observe(this, Observer {
            this.finish()
        })

        viewModel.clearButtonPressed.observe(this, Observer {
            searchEditText.clearFocus()
            searchEditText.text.clear()
        })


        viewModel.loadHistoryTracks()

    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showSearchResult(state.tracks)
            is SearchState.Empty -> showEmpty(state.responseState)
            is SearchState.Error -> showError(state.responseState)
            is SearchState.Loading -> showLoading()
            is SearchState.History -> showHistoryLayout(state.tracks)
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun showError(responseState: ResponseState) {
        recyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        loadingIndicator.visibility = View.GONE
        showProblemsLayout(responseState)
    }

    private fun showEmpty(responseState: ResponseState) {
        showProblemsLayout(responseState)
    }

    private fun showSearchResult(tracks: List<Track>) {
        recyclerView.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE
        loadingIndicator.visibility = View.GONE
        hideProblemsLayout()

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }


    override fun onDestroy() {
        super.onDestroy()
        // TODO убить clicklistener
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

    private fun hideProblemsLayout() {
        recyclerView.visibility = View.VISIBLE
        problemsLayout.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showHistoryLayout(tracks: List<Track>) {
        historyLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        problemsLayout.visibility = View.GONE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun hideHistoryLayout() {
        recyclerView.visibility = View.VISIBLE
        historyLayout.visibility = View.GONE

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun setupListeners() {
        // Вызовите метод ViewModel при нажатии на кнопку
        backButton.setOnClickListener {
            viewModel.onBackButtonPressed()
        }

        clearButton.setOnClickListener {
            viewModel.onClearButtonPressed()
        }
    }


}