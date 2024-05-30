package com.dicoding.storyapp.ui.landing.register

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
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.utils.ViewModelFactory

class RegisterFragment : Fragment() {
    private val registerViewModel by viewModels<RegisterViewModel> { ViewModelFactory.getAuthInstance(requireContext()) }
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btRegister.setOnClickListener {
            registerViewModel.register(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            ).observe(requireActivity()) { result ->
                if(result != null) {
                    when(result){
                        is Result.Error -> {
                            showToast(result.error)
                            binding.pbRegister.visibility = View.GONE
                        }
                        Result.Loading -> {
                            binding.pbRegister.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            showToast(resources.getString(R.string.register_success))
                            binding.pbRegister.visibility = View.GONE
                            Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
                        }
                    }
                }
            }
        }

        binding.btLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment)
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