package com.digitalawesome.clearchoice.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.digitalawesome.clearchoice.R
import com.digitalawesome.clearchoice.databinding.ActivityAuthBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModel()

    //region Lifecycle Overrides
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_auth) as NavHostFragment
        navController = navHostFragment.navController

        setupViews()
        setupViewEvents()
        setupDataEvents()
    }

    private fun setupViews() {

    }

    private fun setupViewEvents() {

    }

    private fun setupDataEvents() {
        viewModel.uiState.observe(this) {
            when(it) {
                is AuthUiState.LoginSuccess -> {
                    finish()
                }
                else -> Unit
            }
        }
    }

    fun showLoadingIcon() {
//        binding.loadingIcon.loadingWrapper.show()
    }

    fun hideLoadingIcon() {
//        binding.loadingIcon.loadingWrapper.hide()
    }

}
