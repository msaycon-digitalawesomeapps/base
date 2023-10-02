package com.digitalawesome.recweed.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.digitalawesome.recweed.databinding.FragmentProductDetailsBinding
import com.digitalawesome.recweed.home.HomeViewModel
import com.digitalawesome.dispensary.components.utils.toCurrency
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProductDetailsFragment : Fragment(), AddToCartBottomSheetDialog.Listener {

    companion object {
        fun newInstance() = ProductDetailsFragment()
    }

    private lateinit var viewModel: ProductDetailsViewModel
    lateinit var binding: FragmentProductDetailsBinding
    private val homeViewModel: HomeViewModel by sharedViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewEvents()
        setupDataEvents()

    }

    private fun setupViewEvents() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btAddToCart.setOnClickListener {
            AddToCartBottomSheetDialog.newInstance()
                .show(childFragmentManager, AddToCartBottomSheetDialog::class.simpleName)
//            findNavController().navigate(R.id.action_product_details_to_shopping_cart)
        }
    }

    private fun setupDataEvents() {
        homeViewModel.activeProduct.observe(viewLifecycleOwner) {
            binding.tvProductName.text = it.productName
            binding.tvDescription.text = it.description
            binding.tvProductSeller.text = it.brand.name
            binding.tvProductOriginalPrice.text = it.variants.first().basePrice.rec.toCurrency()
            binding.tvProductFinalPrice.text =
                it.variants.first().specialPrice.rec?.let { it.toCurrency() }
                    ?: binding.tvProductOriginalPrice.text

            if (binding.tvProductOriginalPrice.text == binding.tvProductFinalPrice.text) {
                binding.tvProductOriginalPrice.isVisible = false
                binding.vProductOriginalPriceSlash.isVisible = false

            }
            binding.ivProductImage.load(it.imageUrls.firstOrNull())

            val thcBadge = binding.badgeThc
            val cbdBadge = binding.badgeCbd

            thcBadge.badge = "THC ${it.potency.thc.amount}"
            cbdBadge.badge = "CBD ${it.potency.cbd.amount}"

            if (it.potency.thc.amount.isNullOrEmpty()) {
                thcBadge.isVisible = false
            }

            if (it.potency.cbd.amount.isNullOrEmpty()) {
                cbdBadge.isVisible = false
            }
//
//            val rating = (it.aggregateRating ?: 1).toInt().coerceIn(0, 4)
//
//            if (rating >= 1) {
//                (0..rating).forEach { i ->
//                    (binding.vConsolidatedRatings.getChildAt(i) as ImageView).setImageResource(com.digitalawesome.dispensary.components.R.drawable.da_components_ic_star)
//                }
//            }

//            binding.tvReviewCount.text = "(${it.reviewCount} reviews)"

        }
    }

    private fun setupViews() {

    }

    override fun addToCart() {
        ItemAddedToCartBottomSheetDialog.newInstance()
            .show(childFragmentManager, ItemAddedToCartBottomSheetDialog::class.simpleName)
    }

}