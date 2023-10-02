package com.digitalawesome.recweed.home.stores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.digitalawesome.recweed.UserViewModel
import com.digitalawesome.recweed.databinding.FragmentStoresWithSearchBinding
import com.digitalawesome.recweed.store2
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoresWithSearchFragment : Fragment() {

    companion object {
        fun newInstance() = StoresWithSearchFragment()
    }

    private lateinit var binding: FragmentStoresWithSearchBinding
    private val userViewModel: UserViewModel by viewModel()
    private val storesViewModel: StoresViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoresWithSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewEvents()
        setupDataEvents()

        initialServiceCalls()
    }

    private fun setupViews() {
        binding.rvStores.withModels {
            storesViewModel.stores.value?.let {
                it.data.forEach { data ->
                    store2 {
                        id(data.id)
                        title(data.attributes?.name ?: "")
                        image("https://www.shutterstock.com/shutterstock/photos/1364355605/display_1500/stock-photo-green-cannabis-leaves-isolated-on-white-background-growing-medical-marijuana-1364355605.jpg")
                        address(data.attributes?.address ?: "")
                    }
                }
            }
        }
    }

    private fun setupViewEvents() {

    }

    private fun setupDataEvents() {
        storesViewModel.stores.observe(viewLifecycleOwner) {
            binding.rvStores.requestModelBuild()
        }
    }

    private fun initialServiceCalls() {
        storesViewModel.loadStores()
    }
}