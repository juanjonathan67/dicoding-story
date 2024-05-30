package com.dicoding.storyapp.ui.landing.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.ViewModelFactory

class LoginFragment : Fragment() {
    private val loginViewModel by viewModels<LoginViewModel> { ViewModelFactory.getAuthInstance(requireContext()) }
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btLogin.setOnClickListener {
            loginViewModel.login(
                binding.edLoginEmail.text.toString(),
                binding.edLoginPassword.text.toString()
            ).observe(requireActivity()) {result ->
                if(result != null) {
                    when(result){
                        is Result.Error -> {
                            showToast(result.error)
                            binding.pbLogin.visibility = View.GONE
                        }
                        Result.Loading -> {
                            binding.pbLogin.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            showToast(resources.getString(R.string.login_success))
                            binding.pbLogin.visibility = View.GONE
                            val mainIntent = Intent(requireActivity(), MainActivity::class.java)
                            mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(mainIntent)
                        }
                    }
                }
            }
        }

        binding.btRegister.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment)
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}