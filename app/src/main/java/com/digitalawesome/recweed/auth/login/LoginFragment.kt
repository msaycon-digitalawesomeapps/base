package com.digitalawesome.recweed.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalawesome.recweed.R
import com.digitalawesome.recweed.auth.AuthUiState
import com.digitalawesome.recweed.auth.AuthViewModel
import com.digitalawesome.recweed.databinding.FragmentLoginBinding
import io.intercom.android.sdk.utilities.SimpleTextWatcher
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewEvents()
        setupDataEvents()
    }

    private fun setupViews() {

    }

    private fun setupViewEvents() {
        binding.btLogin.setOnClickListener {
            binding.btLogin.isLoading = true
            binding.tfPassword.clearFocus()
            viewModel.login(binding.tfUsername.text, binding.tfPassword.text)
        }

        binding.btSignup.setOnClickListener {
            binding.tfPassword.isError = false
            findNavController().navigate(R.id.action_login_to_signup)
        }

        binding.btForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot_password)
        }

        binding.tfPassword.addTextChangeListener(object : SimpleTextWatcher() {
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tfPassword.isError = false
            }
        })
    }

    private fun setupDataEvents() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            when(it) {
                is AuthUiState.LoginError -> {
                    binding.btLogin.isLoading = false
                    binding.tfPassword.isError = true
                    binding.tfPassword.errorText = it.errorType.message
                }
                else -> {

                }
            }
        }
    }
}