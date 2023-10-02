package com.digitalawesome.recweed.auth.login.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.digitalawesome.dispensary.domain.application.FieldErrors
import com.digitalawesome.recweed.auth.AuthUiState
import com.digitalawesome.recweed.auth.AuthViewModel
import com.digitalawesome.recweed.databinding.FragmentSignupBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignupFragment : Fragment() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var binding: FragmentSignupBinding
    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
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
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btSignup.setOnClickListener {
            binding.btSignup.isLoading = true
            authViewModel.signUp(
                firstName = binding.tfFirstName.text,
                lastName = binding.tfLastName.text,
                phone = binding.tfPhone.text,
                email = binding.tfEmail.text,
                password = binding.tfPassword.text,
                passwordConfirmation = binding.tfConfirmPassword.text,
                code = binding.tfReferralCode.text,
                optInEmail = 0,
                optInText = 0,
                state = binding.tfState.text,
            )
        }
    }

    private fun setupDataEvents() {
        authViewModel.uiState.observe(viewLifecycleOwner) {
            when(it) {
                is AuthUiState.SignupError -> {
                    binding.btSignup.isLoading = false
                    binding.tfReferralCode.isError = true
                    when (it.error) {
                        is FieldErrors -> {
                            it.error.errors?.forEach {
                                val message = it.value.first()
                                when(it.key.trim()) {
                                    "last_name" -> {
                                        binding.tfLastName.isError = true
                                        binding.tfLastName.errorText = message
                                    }
                                    "first_name" -> {
                                        binding.tfFirstName.isError = true
                                        binding.tfFirstName.errorText = message
                                    }
                                    "email" -> {
                                        binding.tfEmail.isError = true
                                        binding.tfEmail.errorText = message
                                    }
                                    "password" -> {
                                        binding.tfPassword.isError = true
                                        binding.tfConfirmPassword.isError = true
                                        binding.tfPassword.errorText = message
                                        binding.tfConfirmPassword.errorText = message
                                    }
                                    "phone" -> {
                                        binding.tfPhone.isError = true
                                        binding.tfPhone.errorText = message
                                    }
                                    "residence_state" -> {
                                        binding.tfState.isError = true
                                        binding.tfState.errorText = message
                                    }
                                }
                            }
                        }

                    }

                }
                else -> {

                }
            }
        }
    }
}