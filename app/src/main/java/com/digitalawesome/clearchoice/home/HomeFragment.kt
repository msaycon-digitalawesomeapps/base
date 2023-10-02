package com.digitalawesome.clearchoice.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.carousel
import com.digitalawesome.clearchoice.ListingBindingModel_
import com.digitalawesome.clearchoice.R
import com.digitalawesome.clearchoice.VibeBindingModel_
import com.digitalawesome.clearchoice.category
import com.digitalawesome.clearchoice.databinding.FragmentHomeBinding
import com.digitalawesome.clearchoice.home.stores.StoresViewModel
import com.digitalawesome.clearchoice.shopPromotionTile
import com.digitalawesome.dispensary.components.models.UiState
import com.digitalawesome.dispensary.components.views.atoms.badge.Badge
import com.digitalawesome.dispensary.domain.models.PromotionsModel
import com.digitalawesome.dispensary.domain.models.PromotionsResponse
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    interface Listener {
        fun showFilter()
        fun dismissFilter()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var listener: Listener? = null
    lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val storesViewModel: StoresViewModel by sharedViewModel()
    private var selectedVibe = ""
    private var promos: List<PromotionsModel> = listOf()

    private val numberFormatter by lazy {
        NumberFormat.getCurrencyInstance(Locale.US)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewEvents()
        setupDataEvents()
    }

    private fun initialServiceCalls() {
        if (!homeViewModel.homeProductsLoaded) {
            homeViewModel.loadHomePageProducts(false)
            homeViewModel.loadProducts()
            homeViewModel.loadPromotions()
        }
    }

    private fun setupDataEvents() {
        homeViewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Initial -> Unit
                is UiState.InProgress -> Unit
                is UiState.Success -> {
                    Log.w(tag, "home products changed, passing to handleProducts()")
                    binding.recyclerView.requestModelBuild()
                }

                is UiState.Error -> {

                }
            }
        }

        homeViewModel.categoriesAndProducts.observe(viewLifecycleOwner) { response ->

        }

        homeViewModel.promotions.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                Log.w(tag, "promotions are null")
                return@observe
            }
            handlePromotionData(response)
        }

        storesViewModel.selectedStore.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                homeViewModel.loadHomePageProducts(true)
                homeViewModel.loadProducts()
                homeViewModel.loadPromotions()
                binding.vStores.text = response.attributes?.name

            }
        }
    }

    private fun handlePromotionData(promoResponse: PromotionsResponse) {
        val sortedAndFilteredPromotions =
            promoResponse.data?.sortedByDescending { it.attributes?.priority }
                ?.filter { it.attributes?.isEnabled == true } ?: return

        val promoBannerToShow: PromotionsModel? = null
        val priorityPromo: PromotionsModel? = null
        val shopPromos = mutableListOf<PromotionsModel>()

        processPromotions(sortedAndFilteredPromotions, shopPromos, promoBannerToShow, priorityPromo)

        handleFullScreenPromo(promoBannerToShow)
        handleFullWidthBanner(sortedAndFilteredPromotions)
        handleShopPromos(sortedAndFilteredPromotions)
    }

    private fun processPromotions(
        promotions: List<PromotionsModel>,
        shopPromos: MutableList<PromotionsModel>,
        promoBannerToShow: PromotionsModel?,
        priorityPromo: PromotionsModel?
    ) {
        var mostRecentPromoBannerDate = 0L
        var priorityBannerPrio = 0

        promotions.forEach { promotion ->
            // Existing logic for populating promoBannerToShow, priorityPromo and shopPromos
        }
    }

    private fun handleFullScreenPromo(promo: PromotionsModel?) {
        // Existing logic for full-screen promo
    }

    private fun handleFullWidthBanner(promos: List<PromotionsModel>) {

    }

    private fun handleShopPromos(promos: List<PromotionsModel>) {
        this.promos =
            promos.filter { it.attributes != null }.filter { it.attributes?.enabledToday() == true }
                .filter { !it.attributes?.homeTileBanner.isNullOrEmpty() }
    }

    private fun setupViews() {
        binding.vStores.text = storesViewModel.savedSelectedStore?.attributes?.name

        binding.rvShopPromo.withModels {
            promos.forEach {
                shopPromotionTile {
                    id(it.id)
                    title(it.attributes?.title ?: "")
                    image(it.attributes?.homeTileBanner ?: "")
                }
            }
        }

        binding.recyclerView.withModels {
            var activitiesIcons = mapOf(
                "Ease my mind" to R.drawable.ic_sleep,
                "Get relief" to R.drawable.ic_celebrate,
                "Get some sleep" to R.drawable.ic_sleep,
                "Stimulate my mind" to R.drawable.ic_ghost,
                "Hang with friends" to R.drawable.ic_friends,
                "Get Active" to R.drawable.ic_fireworks,
                "Get intimate" to R.drawable.ic_heart
            )
            var feelingsIcons = mapOf(
                "Relaxed" to R.drawable.ic_app_icon,
                "Blissful" to R.drawable.ic_app_icon,
                "Pain free" to R.drawable.ic_app_icon,
                "Creative" to R.drawable.ic_app_icon,
                "Sleepy" to R.drawable.ic_app_icon,
                "Energetic" to R.drawable.ic_app_icon,
                "Hungry" to R.drawable.ic_app_icon,
                "Not high" to R.drawable.ic_app_icon
            )

            val vibes = (activitiesIcons + feelingsIcons).map {
                VibeBindingModel_().id(it.key).isActive(it.key == selectedVibe).title(it.key)
                    .onClick { _ ->
                        selectedVibe = it.key
                        binding.recyclerView.requestModelBuild()
                    }
            }

            carousel {
                id("grid carousel")
                numViewsToShowOnScreen(5f)
                models(vibes)
            }

            homeViewModel.categoriesAndProducts.value?.forEach {
                category {
                    id("category ${it.key}")
                    category(it.key)
                    spanSizeOverride { totalSpanCount, position, itemCount ->
                        totalSpanCount
                    }
                }

                val products = it.value.map { product ->
                    val basePrice = product.variants.first().basePrice.rec
                    val specialPrice = product.variants.first().specialPrice.rec
                    ListingBindingModel_().id(product.productId).isDiscounted(false)
                        .image(product.imageUrls.firstOrNull()).title(product.productName)
                        .originalPrice(numberFormatter.format(basePrice))
                        .finalPrice(specialPrice?.let { numberFormatter.format(it) }
                            ?: numberFormatter.format(basePrice)).description(product.description)
                        .onBind { model, view, position ->
                            val thcBadge = view.dataBinding.root.findViewById<Badge>(R.id.badge_1)
                            val cbdBadge = view.dataBinding.root.findViewById<Badge>(R.id.badge_2)
                            thcBadge.badge = "THC ${product.potency.thc.amount}"
                            cbdBadge.badge = "CBD ${product.potency.cbd.amount}"

                            if (product.potency.thc.amount.isNullOrEmpty()) {
                                thcBadge.isVisible = false
                            }

                            if (product.potency.cbd.amount.isNullOrEmpty()) {
                                cbdBadge.isVisible = false
                            }
                        }.onClick { _ ->
                            homeViewModel.updateActive(product)
                            findNavController().navigate(R.id.action_home_to_product_details)
                        }
                }

                carousel {
                    id("category items ${it.key}")
                    numViewsToShowOnScreen(2f)
                    spanSizeOverride { totalSpanCount, position, itemCount ->
                        totalSpanCount
                    }
                    models(products)

                }

            }
        }
    }

    private fun setupViewEvents() {
        binding.vStores.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_store)
        }

        binding.btFilter.setOnClickListener {
            listener?.showFilter()
//            THCFilterBottomSheetDialog.newInstance()
//                .show(childFragmentManager, THCFilterBottomSheetDialog::class.simpleName)
        }

        binding.sbSearch.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_product_search)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            homeViewModel.loadHomePageProducts(true)
            homeViewModel.loadProducts()
            homeViewModel.loadPromotions()
        }

        binding.toolbar.setOnMenuItemClickListener {

            if (it.itemId == R.id.navigation_cart) {
                findNavController().navigate(R.id.action_product_details_to_shopping_cart)
            }
            true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? Listener
    }
}


