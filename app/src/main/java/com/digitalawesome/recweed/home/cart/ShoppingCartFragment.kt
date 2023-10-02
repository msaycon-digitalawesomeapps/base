package com.digitalawesome.recweed.home.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.digitalawesome.recweed.R
import com.digitalawesome.recweed.UserViewModel
import com.digitalawesome.recweed.cartItem
import com.digitalawesome.recweed.category
import com.digitalawesome.recweed.databinding.FragmentShoppingCartBinding
import com.digitalawesome.recweed.databinding.ViewHolderCartItemBinding
import com.digitalawesome.recweed.home.HomeViewModel
import com.digitalawesome.recweed.listing
import com.digitalawesome.dispensary.components.utils.toCurrency
import com.digitalawesome.dispensary.components.views.atoms.badge.Badge
import com.digitalawesome.dispensary.domain.models.CartModel
import com.digitalawesome.dispensary.domain.models.ProductAttributes
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ShoppingCartFragment : Fragment() {

    companion object {
        fun newInstance() = ShoppingCartFragment()
    }

    private var priceForCartId: MutableMap<Int, String> = mutableMapOf()
    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var binding: FragmentShoppingCartBinding
    private val userViewModel: UserViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var cart: List<CartModel> = listOf()
    private var products: List<ProductAttributes> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewEvents()
        setupDataEvents()

        initialServiceCalls()

    }

    private fun initialServiceCalls() {
        if (userViewModel.cart.value.isNullOrEmpty()) {
            userViewModel.getCart()
        }
    }

    private fun setupViews() {
        binding.rvCartItems.withModels {
            cart.forEach {
                cartItem {
                    id("cartItem${it.id}")
                    isDiscounted(false)
                    title(it.relationships.product.attributes.productName)
                    image(it.relationships.product.attributes.imageUrls.firstOrNull())
                    originalPrice(priceForCartId[it.id])
                    finalPrice(priceForCartId[it.id])
                    description(it.relationships.product.attributes.description)
                    spanSizeOverride { totalSpanCount, position, itemCount ->
                        totalSpanCount
                    }
                    onBind { model, view, position ->
                        val cartBinding = view.dataBinding as ViewHolderCartItemBinding
                        val priceId = it.attributes.priceId
                        cartBinding.qiQuantity.max =
                            it.relationships.product.attributes.variants.find { it.option == priceId }?.quantity
                                ?: 1
                        cartBinding.qiQuantity.value = it.attributes.count
                    }
                    onClick { _ ->
                        findNavController().navigate(R.id.action_search_to_product_details)
                    }
                }
            }

            category {
                id("youmayalsolike")
                category("You may also like")
                spanSizeOverride { totalSpanCount, position, itemCount ->
                    totalSpanCount
                }
            }
            products.take(4).forEach { product ->
                val basePrice = product.variants.first().basePrice.rec
                val specialPrice = product.variants.first().specialPrice.rec

                listing {
                    id(product.productId)
                    isDiscounted(false)
                    image(product.imageUrls.firstOrNull())
                    title(product.productName)
                    originalPrice(basePrice.toCurrency())
                    finalPrice(specialPrice?.let { it.toCurrency() } ?: basePrice.toCurrency())
                    description(product.description)
                    onBind { model, view, position ->
                        view.dataBinding.root.findViewById<Badge>(R.id.badge_1).badge =
                            "CBD ${product.potency.cbd.amount}"
                        view.dataBinding.root.findViewById<Badge>(R.id.badge_2).badge =
                            "THC ${product.potency.thc.amount}"
                    }
                    onClick { _ ->
                        homeViewModel.updateActive(product)
                        findNavController().navigate(R.id.action_home_to_product_details)
                    }
                }
            }

        }


    }

    private fun setupDataEvents() {
        userViewModel.cart.observe(viewLifecycleOwner) {
            cart = it
            cart.forEach { cartItem ->
                priceForCartId[cartItem.id] =
                    ((cartItem.relationships.product.attributes.variants.find { it.option == cartItem.attributes.priceId }?.basePrice?.rec
                        ?: 0.0) * cartItem.attributes.count).toCurrency()
            }
            binding.rvCartItems.requestModelBuild()
            binding.rvCartItems.scrollToPosition(0)
        }

        homeViewModel.products.observe(viewLifecycleOwner) {
            products = it
            binding.rvCartItems.requestModelBuild()
        }

    }

    private fun setupViewEvents() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

}