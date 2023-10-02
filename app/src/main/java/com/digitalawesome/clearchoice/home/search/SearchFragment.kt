package com.digitalawesome.clearchoice.home.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.digitalawesome.dispensary.components.views.atoms.badge.Badge
import com.digitalawesome.dispensary.components.views.fragments.THCFilterBottomSheetDialog
import com.digitalawesome.clearchoice.R
import com.digitalawesome.clearchoice.databinding.FragmentSearchBinding
import com.digitalawesome.clearchoice.home.HomeViewModel
import com.digitalawesome.clearchoice.listing
import com.digitalawesome.clearchoice.listingFull
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.Locale

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var isGrid = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vStores.setOnClickListener {
            findNavController().navigate(R.id.action_search_to_store)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
                    }

        binding.btFilter.setOnClickListener {
            THCFilterBottomSheetDialog.newInstance().show(childFragmentManager, THCFilterBottomSheetDialog::class.simpleName)
        }

        binding.btViewMode.setOnClickListener {
            isGrid = !isGrid
            binding.rvListings.requestModelBuild()
        }

        binding.rvListings.withModels {
            val numberFormatter = NumberFormat.getCurrencyInstance(Locale.US)

            homeViewModel.categoriesAndProducts.value?.values?.flatten()?.forEach  { product ->
                val basePrice = product.variants.first().basePrice.rec
                val specialPrice = product.variants.first().specialPrice.rec
                if (isGrid) {
                    listing {
                        id(product.productId)
                        isDiscounted(false)
                        image(product.imageUrls.firstOrNull() )
                        title(product.productName)
                        originalPrice(numberFormatter.format(basePrice))
                        finalPrice(specialPrice?.let { numberFormatter.format(it) })
                        description(product.description)
                        onBind { model, view, position ->
                            view.dataBinding.root.findViewById<Badge>(R.id.badge_1).badge = "CBD ${product.potency.cbd.amount}"
                            view.dataBinding.root.findViewById<Badge>(R.id.badge_2).badge = "THC ${product.potency.thc.amount}"
                        }
                        onClick { _ ->
                            homeViewModel.updateActive(product)
                            findNavController().navigate(R.id.action_home_to_product_details)
                        }
                    }
                } else {
                    listingFull {
                        id(product.productId)
                        isDiscounted(false)
                        image(product.imageUrls.firstOrNull())
                        title(product.productName)
                        originalPrice(numberFormatter.format(basePrice))
                        finalPrice(specialPrice?.let { numberFormatter.format(it) })
                        description(product.description)
                        spanSizeOverride { totalSpanCount, position, itemCount ->
                            totalSpanCount
                        }
                        onBind { model, view, position ->
                            view.dataBinding.root.findViewById<Badge>(R.id.badge_1).badge = "CBD ${product.potency.cbd.amount}"
                            view.dataBinding.root.findViewById<Badge>(R.id.badge_2).badge = "THC ${product.potency.thc.amount}"
                        }
                        onClick { _ ->
                            homeViewModel.updateActive(product)
                            findNavController().navigate(R.id.action_home_to_product_details)
                        }
                    }
                }
            }
        }
    }

}