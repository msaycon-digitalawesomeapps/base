package com.digitalawesome.recweed.home.product

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.digitalawesome.dispensary.components.utils.toCurrency
import com.digitalawesome.dispensary.components.views.atoms.controls.QuantityInput
import com.digitalawesome.recweed.databinding.FragmentAddToCartBinding
import com.digitalawesome.dispensary.components.views.molecules.bottomsheet.StandardHeaderBottomSheet
import com.digitalawesome.dispensary.domain.models.ProductAttributes
import com.digitalawesome.dispensary.domain.models.Variant
import com.digitalawesome.recweed.UserViewModel
import com.digitalawesome.recweed.databinding.ViewHolderChipBinding
import com.digitalawesome.recweed.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Use the [AddToCartBottomSheetDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddToCartBottomSheetDialog : StandardHeaderBottomSheet() {

    private var listener: Listener? = null
    private var isFavorite: Boolean = false
    private lateinit var selectedVariant: Variant
    private lateinit var activeProduct: ProductAttributes
    lateinit var binding: FragmentAddToCartBinding
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val userViewModel: UserViewModel by sharedViewModel()

    interface Listener {
        fun addToCart()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddToCartBottomSheetDialog()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?): View {
        binding = FragmentAddToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataEvents()
    }

    private fun setupDataEvents() {
        homeViewModel.activeProduct.observe(viewLifecycleOwner) {
            activeProduct = it
            isFavorite = it.isFavorite
            binding.btFavorite.icon = ContextCompat.getDrawable(requireContext(), if (it.isFavorite) com.digitalawesome.dispensary.components.R.drawable.da_components_ic_heart_filled else com.digitalawesome.dispensary.components.R.drawable.da_components_ic_heart)
            it.variants.forEachIndexed { index, variant ->
                val chipBinding = ViewHolderChipBinding.inflate(layoutInflater)
                chipBinding.vSizeLabel.text = variant.option
                val chipId = View.generateViewId()
                chipBinding.vSizeLabel.id = chipId
                chipBinding.root.setOnClickListener {
                    selectedVariant = variant
                    binding.tvTotal.text = (binding.qiQuantity.value * variant.basePrice.rec).toCurrency()
                    binding.qiQuantity.max = variant.quantity
                }

                binding.cgSizes.addView(chipBinding.root)

                if (index == 0) {
                    binding.cgSizes.check(chipId)
                    selectedVariant = variant
                    binding.qiQuantity.max = variant.quantity
                    binding.tvTotal.text = variant.basePrice.rec.toCurrency()
                }

            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        binding.qiQuantity.value = 1
        binding.qiQuantity.listener = object : QuantityInput.Listener {
            override fun onValueChange(newValue: Int) {
                binding.btAddToCart.isEnabled = newValue > 0
                binding.tvTotal.text = NumberFormat.getCurrencyInstance(Locale.US).format(newValue.toDouble() * (homeViewModel.activeProduct.value?.variants?.first()?.basePrice?.rec ?: 0.0))
            }
        }
    }

    override fun setupViewEvents() {
        super.setupViewEvents()

        binding.btFavorite.setOnClickListener {
            if (isFavorite) {
                userViewModel.removeFromFavorites(activeProduct.productId)
            } else {
                userViewModel.addToFavorites(activeProduct.productId)
            }
            isFavorite = !isFavorite

            binding.btFavorite.icon = ContextCompat.getDrawable(requireContext(), if (isFavorite) com.digitalawesome.dispensary.components.R.drawable.da_components_ic_heart_filled else com.digitalawesome.dispensary.components.R.drawable.da_components_ic_heart)
        }

        binding.btAddToCart.setOnClickListener {
            userViewModel.addToCart(activeProduct.productId, selectedVariant.option, binding.qiQuantity.value)
            dismiss()
            listener?.addToCart()
        }
    }

    override fun getTitle(): String = ""

    override fun getDescription() = null

    override fun hasCloseButton(): Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }
}