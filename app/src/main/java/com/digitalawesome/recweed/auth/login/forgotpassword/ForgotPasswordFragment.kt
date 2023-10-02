package com.digitalawesome.recweed.auth.login.forgotpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.digitalawesome.recweed.auth.AuthViewModel
import com.digitalawesome.recweed.databinding.FragmentForgotPasswordBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    private lateinit var binding: FragmentForgotPasswordBinding
    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewEvents()
        setupDataEvents()
    }

    private fun setupViews() {
        binding.tfEmail.text = authViewModel.lastEmailInput
    }

    private fun setupViewEvents() {
        binding.btLogin.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setupDataEvents() {


    }
}