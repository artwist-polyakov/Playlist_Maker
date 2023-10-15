package com.example.playlistmaker.media.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.common.presentation.models.TrackToTrackDtoMapper
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragments.SearchFragment
import com.example.playlistmaker.search.ui.fragments.TracksAdapter
import com.example.playlistmaker.search.ui.view_model.FavoriteState
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 10L
    }

    private var onTrackClickDebounce: (Track) -> Unit = {}

    private var adapter: TracksAdapter? = TracksAdapter(
        object : TracksAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }
        }
    )

    private val viewModel by viewModel<FavoritesViewModel>()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!


    private lateinit var problemsLayout: LinearLayout
    private lateinit var favoritesList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        problemsLayout = binding.problemsLayout
        favoritesList = binding.favoritesRecyclerView
        favoritesList.adapter = adapter

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.saveTrackToHistory(TrackToTrackDtoMapper().invoke(track))
            val intent = Intent(context, PlayerActivity::class.java)
            startActivity(intent)
        }

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onTrackClickDebounce = {}
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.FAVORITES -> showContent(state.favorites)
            is FavoriteState.NOTHING_TO_SHOW -> showEmpty()
            is FavoriteState.LOADING -> showLoading()
        }
    }

    private fun showLoading() {
        favoritesList.visibility = View.GONE
        problemsLayout.visibility = View.GONE
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        favoritesList.visibility = View.GONE
        binding.loadingIndicator.visibility = View.GONE
        problemsLayout.visibility = View.VISIBLE
    }

    private fun showContent(favorites: List<Track>) {
        binding.loadingIndicator.visibility = View.GONE
        problemsLayout.visibility = View.GONE
        favoritesList.visibility = View.VISIBLE
        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(favorites)
        adapter?.notifyDataSetChanged()
    }
}
