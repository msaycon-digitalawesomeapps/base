package com.digitalawesome.recweed


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.digitalawesome.dispensary.components.utils.showModalView
import com.digitalawesome.dispensary.domain.AUTH_TOKEN
import com.digitalawesome.dispensary.domain.IS_NOTIFICATION
import com.digitalawesome.dispensary.domain.NOTIF_ACHIEVEMENT_ID
import com.digitalawesome.dispensary.domain.NOTIF_ORDER_ID
import com.digitalawesome.dispensary.domain.NOTIF_TYPE
import com.digitalawesome.dispensary.domain.PREF_KEY_AGE_VERIFIED
import com.digitalawesome.dispensary.domain.PUBLIC_TOKEN
import com.digitalawesome.dispensary.domain.services.SharedPrefsService
import com.digitalawesome.recweed.auth.AuthActivity
import com.digitalawesome.recweed.databinding.ActivityWelcomeBinding
import com.digitalawesome.recweed.home.MainActivity
import com.digitalawesome.recweed.permissions.LocationAccessFragment
import com.digitalawesome.recweed.utils.displayAgeAlert
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity : AppCompatActivity(), LocationAccessFragment.Listener {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var splash: SplashScreen
    private var initializeAttempts = 1

    private val sharedPreference: SharedPrefsService by inject()
    private val userViewModel: UserViewModel by viewModel()

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            initializeUser()
        }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeData()
        //TODO: update splash screen to comply with android's design pattern, for now just display a static image
        splash = installSplashScreen()
        splash.setKeepOnScreenCondition { false }
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btGetStarted.setOnClickListener { verifyAge() }

        setupDataEvents()

        if (sharedPreference.getString(AUTH_TOKEN) != null) {
            binding.btGetStarted.isVisible = false
            verifyAge()
        } else {
            binding.btGetStarted.isVisible = true
        }
    }

    private fun setupDataEvents() {
//        userViewModel.userData.observe(this) {
////            if (error != null && error == "Unauthorized") {
////                sharedPreference.remove(AUTH_TOKEN)
////                initializeUser()
////                return@getCurrentUser
////            }
//
//        }
        userViewModel.userData.observe(this) {
            if (it == null) return@observe
            val newIntent = handleIntent(intent)
            sharedPreference.remove(PUBLIC_TOKEN)
            loadMainActivity(newIntent)
        }
    }

    private fun initializeData() {
//        if (sharedPreference.getString(PREFERRED_STORE_ID) == null) {
//            sharedPreference.putString(PREFERRED_STORE_ID, BuildConfig.JANE_STORE_ID)
//        }
    }

    private fun verifyAge() {
        if (userViewModel.isAgeVerified()) {
            if (isLocationPermissionGranted()) {
                initializeUser()
            } else {
                showModalView(LocationAccessFragment.newInstance(), binding.root.id)
            }
        } else {
            displayAgeAlert(title = "Are you old enough\nto be here?",
                message = getString(R.string.lbl_age_verification_desc),
                cancelButtonText = getString(R.string.lbl_cancel),
                cancelButtonAction = {},
                confirmButtonText = getString(R.string.lbl_confirm),
                confirmButtonAction = {
                    sharedPreference.putBoolean(PREF_KEY_AGE_VERIFIED, true)
                    if (isLocationPermissionGranted()) {
                        initializeUser()
                    } else {
                        showModalView(LocationAccessFragment.newInstance(), binding.root.id)
                    }
                })
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun initializeUser() {
        if (sharedPreference.getString(AUTH_TOKEN) != null) {
            userViewModel.getServerSettings { response, error ->
                if (error != null) {
                    sharedPreference.remove(AUTH_TOKEN)
                    if (initializeAttempts < 4) {
                        initializeAttempts++
                        initializeUser()
                    } else {
                        splash.setKeepOnScreenCondition { false }
                    }
                    return@getServerSettings
                }

                userViewModel.getCurrentUser()
            }

        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()

//            userViewModel.getPublicToken { loginResponse, error ->
//                if (error == null) {
//                    sharedPreference.putString(PUBLIC_TOKEN, loginResponse?.accessToken)
//                    loadMainActivity(handleIntent(intent))
//                } else {
//
//                }
//            }
        }
    }

    private fun loadMainActivity(intent: Intent) {
        if (sharedPreference.getBoolean(PREF_KEY_AGE_VERIFIED)) {
            startActivity(intent)
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            verifyAge()
        }
    }

    private fun handleIntent(intent: Intent): Intent {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        var bundle = intent.extras
        resultIntent.putExtra(NOTIF_TYPE, bundle?.getString(NOTIF_TYPE))
        resultIntent.putExtra(
            NOTIF_ACHIEVEMENT_ID,
            bundle?.getString(NOTIF_ACHIEVEMENT_ID)?.toInt() ?: 0
        )
        resultIntent.putExtra(NOTIF_ORDER_ID, bundle?.getString(NOTIF_ORDER_ID)?.toInt() ?: 0)
        resultIntent.putExtra(IS_NOTIFICATION, true)
        return resultIntent
    }

    override fun enableButtonClicked() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

}