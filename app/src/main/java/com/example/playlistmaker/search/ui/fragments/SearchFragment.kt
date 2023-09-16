package com.example.playlistmaker.search.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.common.presentation.models.TrackToTrackInformationMapper
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.ResponseState
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class ResponseState {
    SUCCESS,
    NOTHING_FOUND,
    ERROR,
    CLEAR
}

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val adapter = TracksAdapter(
        object : TracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
                    val intent = Intent(context, PlayerActivity::class.java)
                    intent.putExtra("track", TrackToTrackInformationMapper().invoke(track))
                    startActivity(intent)
                }
            }
        }
    )

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchResultsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.clearButtonPressed.observe(viewLifecycleOwner, Observer {
            binding.searchEditText.clearFocus()
            binding.searchEditText.text.clear()
        })
        binding.clearButton.setOnClickListener {
            viewModel.clearHistoryAndHide()
        }
        viewModel.restoreLastState()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
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

    private fun makeClearButtonInvisible() {
        binding.clearIcon.visibility = View.GONE
        binding.searchEditText.background = requireContext().getDrawable(R.drawable.rounded_edittext)
    }

    private fun makeClearButtonVisible() {
        binding.clearIcon.visibility = View.VISIBLE
        binding.clearIcon.background = requireContext().getDrawable(R.drawable.right_rounded_edittext)
        binding.searchEditText.background = requireContext().getDrawable(R.drawable.left_rounded_edittext)
    }

    private fun showProblemsLayout(responseState: ResponseState) {
        binding.searchResultsRecyclerView.visibility = View.GONE
        binding.historyLayout.visibility = View.GONE
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
        binding.clearIcon.setOnClickListener {
            viewModel.onClearButtonPressed()
        }
    }



}