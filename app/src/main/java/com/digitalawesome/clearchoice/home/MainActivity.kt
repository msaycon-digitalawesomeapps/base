package com.digitalawesome.clearchoice.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.digitalawesome.clearchoice.BuildConfig
import com.digitalawesome.clearchoice.R
import com.digitalawesome.clearchoice.auth.AuthActivity
import com.digitalawesome.clearchoice.databinding.ActivityMainBinding
import com.digitalawesome.clearchoice.home.product.AddToCartBottomSheetDialog
import com.digitalawesome.clearchoice.home.product.ItemAddedToCartBottomSheetDialog
import com.digitalawesome.clearchoice.home.stores.StoresFragment
import com.digitalawesome.clearchoice.home.stores.StoresViewModel
import com.digitalawesome.dispensary.components.utils.showDrawerView
import com.digitalawesome.dispensary.components.utils.showModalView
import org.koin.androidx.viewmodel.ext.android.viewModel


// A placeholder Activity, it should be your own activity

class MainActivity : AppCompatActivity(),
    StoresFragment.Listener,
    AddToCartBottomSheetDialog.Listener,
    ItemAddedToCartBottomSheetDialog.Listener,
    HomeFragment.Listener {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val storesViewModel: StoresViewModel by viewModel()

    private var storesChooserDisplayed: Boolean = false
    private val viewModel: MainViewModel by viewModel()
    private var pendingMenuItem: MenuItem? = null

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
            } else {
                pendingMenuItem?.let {
                    NavigationUI.onNavDestinationSelected(it, findNavController(R.id.v_main_root))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.version.text = "v" + BuildConfig.VERSION_NAME

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.v_main_root)
        navController = navHostFragment?.findNavController() ?: return
        if (storesViewModel.savedSelectedStore == null && !storesChooserDisplayed) {
            storesChooserDisplayed = true
            binding.navView.isVisible = false
            showModalView(StoresFragment.newInstance(), binding.root.id)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            pendingMenuItem = menuItem
            if (menuItem.itemId == R.id.account && !viewModel.isLoggedIn()) {
                resultLauncher.launch(Intent(this, AuthActivity::class.java))
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController)
            }
            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.home, R.id.stores_with_search, R.id.account -> binding.navView.isVisible = true
                else -> binding.navView.isVisible = false
            }
        }

        binding.navView.itemIconTintList = null
    }

    override fun storeSelected() {
        supportFragmentManager.popBackStackImmediate()
        binding.navView.isVisible = true
    }

    override fun close() {

    }

    override fun addToCart() {
        ItemAddedToCartBottomSheetDialog.newInstance()
            .show(supportFragmentManager, ItemAddedToCartBottomSheetDialog::class.simpleName)
    }

    override fun checkout() {
        navController.navigate(R.id.action_product_details_to_shopping_cart)
    }

    override fun showFilter() {
        showDrawerView(FilterSidebarDialog.newInstance(), binding.vMainRoot.id)
    }

    override fun dismissFilter() {

    }

}