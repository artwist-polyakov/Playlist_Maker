package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.common.presentation.models.TrackToTrackInformationMapper
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class ResponseState {
    SUCCESS,
    NOTHING_FOUND,
    ERROR,
    CLEAR
}

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = TracksAdapter(
        object : TracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                    val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    intent.putExtra("track", TrackToTrackInformationMapper().invoke(track))
                    startActivity(intent)
                }
            }
        }
    )

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var textWatcher: TextWatcher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchResultsRecyclerView.adapter = adapter
        binding.searchHistoryRecyclerView.adapter = adapter
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

        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.backButtonPressed.observe(this, Observer {
            this.finish()
        })

        viewModel.clearButtonPressed.observe(this, Observer {
            binding.searchEditText.clearFocus()
            binding.searchEditText.text.clear()
        })

        viewModel.loadHistoryTracks()

        binding.clearButton.setOnClickListener {
            viewModel.clearHistoryAndHide()
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.loadHistoryTracks()
        viewModel.restoreLastState()
        adapter.notifyDataSetChanged()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showSearchResult(state.tracks)
            is SearchState.Empty -> showEmpty(state.responseState)
            is SearchState.Error -> showError(state.responseState)
            is SearchState.Loading -> showLoading()
            is SearchState.History -> showHistoryLayout(state.tracks)
            is SearchState.Virgin -> clearAll()
        }
    }

    private fun clearAll() {
        adapter.tracks.clear()
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        hideProblemsLayout()
    }

    private fun showLoading() {
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun showError(responseState: ResponseState) {
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        showProblemsLayout(responseState)
    }

    private fun showEmpty(responseState: ResponseState) {
        showProblemsLayout(responseState)
    }

    private fun showSearchResult(tracks: List<Track>) {
        binding.searchResultsRecyclerView.visibility = View.VISIBLE
        binding.historyLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
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
        binding.clearIcon.visibility = View.GONE
        binding.searchEditText.background = getDrawable(R.drawable.rounded_edittext)
    }

    private fun makeClearButtonVisible() {
        binding.clearIcon.visibility = View.VISIBLE
        binding.clearIcon.background = getDrawable(R.drawable.right_rounded_edittext)
        binding.searchEditText.background = getDrawable(R.drawable.left_rounded_edittext)
    }

    private fun showProblemsLayout(responseState: ResponseState) {
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.problemsLayout.visibility = View.VISIBLE
        when (responseState.name) {
            ResponseState.ERROR.name -> {
                binding.searchResultsRecyclerView.visibility = View.GONE
                binding.loadingIndicator.visibility = View.GONE
                binding.searchPlaceholderText.text = getString(R.string.no_internet)
                binding.problemsImage.setImageResource(R.drawable.no_internet)
                binding.refreshButton.visibility = View.VISIBLE
            }
            ResponseState.NOTHING_FOUND.name -> {
                binding.searchResultsRecyclerView.visibility = View.GONE
                binding.loadingIndicator.visibility = View.GONE
                binding.searchPlaceholderText.text = getString(R.string.nothing_found)
                binding.problemsImage.setImageResource(R.drawable.nothing_found)
                binding.refreshButton.visibility = View.GONE
            }
        }
    }

    private fun hideProblemsLayout() {
        binding.searchResultsRecyclerView.visibility = View.VISIBLE
        binding.problemsLayout.visibility = View.GONE
        binding.refreshButton.visibility = View.GONE
    }

    private fun showHistoryLayout(tracks: List<Track>) {
        binding.historyLayout.visibility = View.VISIBLE
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.problemsLayout.visibility = View.GONE
        binding.clearButton.visibility = View.VISIBLE

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun hideHistoryLayout() {
        binding.searchResultsRecyclerView.visibility = View.VISIBLE
        binding.historyLayout.visibility = View.GONE

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
        binding.returnButton.setOnClickListener {
            viewModel.onBackButtonPressed()
        }
        binding.clearIcon.setOnClickListener {
            viewModel.onClearButtonPressed()
        }
    }
}