package com.dicoding.storyapp.ui.landing.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentLandingBinding

class LandingFragment : Fragment() {
    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btLogin.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_landingFragment_to_loginFragment)
        )

        binding.btRegister.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_landingFragment_to_registerFragment)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}