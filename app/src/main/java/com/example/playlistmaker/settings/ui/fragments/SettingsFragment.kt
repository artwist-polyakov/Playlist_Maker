package com.example.playlistmaker.settings.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.activity.SettingsActivity
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }

        binding.sharingLayout.setOnClickListener {
            viewModel.shareLink()
        }

        binding.supportLayout.setOnClickListener {
            viewModel.sendSupport()
        }

        binding.agreementLayout.setOnClickListener {
            viewModel.openAgreement()
        }


        viewModel.isDarkTheme.observe(viewLifecycleOwner, Observer { isDark ->
            binding.themeSwitcher.isChecked = isDark
        })

        viewModel.themeSwitcherEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            binding.themeSwitcher.setEnabled(isEnabled)
        })

        viewModel.restartActivity.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(requireContext(), requireActivity()::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(0, 0)
                requireActivity().finish()
            }, 500)  // Задержка в 0.5 секунды
        }
    }


}