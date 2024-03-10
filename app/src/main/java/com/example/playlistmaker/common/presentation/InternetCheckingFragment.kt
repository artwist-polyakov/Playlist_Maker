package com.example.playlistmaker.common.presentation

import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class InternetCheckingFragment<VB : ViewBinding>(
    private val inflate: Inflate<VB>
) : Fragment() {
    private val internetConnectionBroadcastReciever = InternetConnectionBroadcastReciever(
        action_internet_unavailabla = {
            binding.root.showCustomSnackbar("Отсутствует подключение к интернету")
        },
        action_internet_available = {
            binding.root.showCustomSnackbar("Подключение к интернету восстановлено")
        }
    )
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): android.view.View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            internetConnectionBroadcastReciever,
            IntentFilter(InternetConnectionBroadcastReciever.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(internetConnectionBroadcastReciever)
    }


}