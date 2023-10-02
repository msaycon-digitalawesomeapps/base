package com.digitalawesome.clearchoice.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalawesome.dispensary.components.models.GenericError
import com.digitalawesome.dispensary.components.models.UiState
import com.digitalawesome.dispensary.domain.CBD_MAX
import com.digitalawesome.dispensary.domain.CBD_MIN
import com.digitalawesome.dispensary.domain.PRICE_MAX
import com.digitalawesome.dispensary.domain.PRICE_MIN
import com.digitalawesome.dispensary.domain.ROOT_TYPE_BESTSELLING
import com.digitalawesome.dispensary.domain.ROOT_TYPE_EDIBLE
import com.digitalawesome.dispensary.domain.ROOT_TYPE_EXTRACT
import com.digitalawesome.dispensary.domain.ROOT_TYPE_FLOWER
import com.digitalawesome.dispensary.domain.ROOT_TYPE_GEAR
import com.digitalawesome.dispensary.domain.ROOT_TYPE_PRE_ROLL
import com.digitalawesome.dispensary.domain.ROOT_TYPE_TINCTURE
import com.digitalawesome.dispensary.domain.ROOT_TYPE_TOPICAL
import com.digitalawesome.dispensary.domain.ROOT_TYPE_VAPE
import com.digitalawesome.dispensary.domain.THC_MAX
import com.digitalawesome.dispensary.domain.THC_MIN
import com.digitalawesome.dispensary.domain.application.DomainResponse
import com.digitalawesome.dispensary.domain.interactors.ProductInteractor
import com.digitalawesome.dispensary.domain.models.FacetsModel
import com.digitalawesome.dispensary.domain.models.ProductAttributes
import com.digitalawesome.dispensary.domain.models.ProductResponse
import com.digitalawesome.dispensary.domain.models.PromotionsResponse
import com.digitalawesome.dispensary.domain.services.SharedPrefsService
import kotlinx.coroutines.launch

class HomeViewModel(
    val productInteractor: ProductInteractor,
    val sharedPrefsService: SharedPrefsService
) : ViewModel() {

    val rootTypes = arrayOf(
        ROOT_TYPE_BESTSELLING,
        ROOT_TYPE_FLOWER, ROOT_TYPE_VAPE,
        ROOT_TYPE_PRE_ROLL, ROOT_TYPE_EXTRACT, ROOT_TYPE_EDIBLE, ROOT_TYPE_TINCTURE,
        ROOT_TYPE_TOPICAL, ROOT_TYPE_GEAR
    )

    var thcMin = THC_MIN
    var thcMax = THC_MAX
    var cbdMin = CBD_MIN
    var cbdMax = CBD_MAX
    var priceMin = PRICE_MIN
    var priceMax = PRICE_MAX
    var ratingMin = 0
    var isThcSet = false
    var isCbdSet = false
    var isPriceSet = false
    var sort = ""
    var categories: MutableList<String> = mutableListOf()
    var feelings: MutableList<String> = mutableListOf()
    var activities: MutableList<String> = mutableListOf()
    var brands: MutableList<String> = mutableListOf()
    var weights: MutableList<String> = mutableListOf()
    var kinds: MutableList<String> = mutableListOf()
    var promotionFilter: MutableList<String> = mutableListOf()
    var specialIds: String? = null
    var keyword: String? = null
    var productFacets: FacetsModel? = null

    var searchMode = false
    var searchTitle: String? = null
    var searchBanner: String? = null

    var loadDetailsFromCart = false

    var homeProductsLoaded = false

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _promotions: MutableLiveData<PromotionsResponse?> = MutableLiveData()
    val promotions: LiveData<PromotionsResponse?> = _promotions

    private val _categoriesAndProducts = MutableLiveData<Map<String, List<ProductAttributes>>>()
    val categoriesAndProducts: LiveData<Map<String, List<ProductAttributes>>> =
        _categoriesAndProducts

    private val _products = MutableLiveData<List<ProductAttributes>>()
    val products: LiveData<List<ProductAttributes>> = _products

    private val _filteredData = MutableLiveData<List<ProductAttributes>>()
    val filteredData: LiveData<List<ProductAttributes>> = _filteredData

    private val _recommendedProducts = MutableLiveData<Pair<ProductResponse?, String?>>()
    val recommendedProducts: LiveData<Pair<ProductResponse?, String?>> = _recommendedProducts

    private val _filterCount: MutableLiveData<Int> = MutableLiveData(0)
    val filterCount: LiveData<Int> = _filterCount
    var filterCounter: Int = 0

    private val _activeProduct: MutableLiveData<ProductAttributes> = MutableLiveData()
    val activeProduct: LiveData<ProductAttributes> = _activeProduct

    private val _hasPendingFilterChanges: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasPendingFilterChanges: LiveData<Boolean> = _hasPendingFilterChanges

    fun setSearchOptions(title: String? = null, banner: String? = null, mode: Boolean = false) {
        searchTitle = title
        searchBanner = banner
        searchMode = mode
    }

    fun updateActive(product: ProductAttributes) {
        _activeProduct.value = product
    }

    fun setPendingFilterChanges() {
        _hasPendingFilterChanges.value = true
    }

    fun consumePendingFilterChanges() {
        _hasPendingFilterChanges.value = false
    }

    fun setSortFilter(sort: String) {
        this.sort = sort
    }

    fun setThcFilter(min: Float, max: Float) {
        thcMax = max
        thcMin = min
        if (!isThcSet) {
            filterCounter++
            _filterCount.value = filterCounter
            isThcSet = true
        }
    }

    fun setCbdFilter(min: Float, max: Float) {
        cbdMax = max
        cbdMin = min
        if (!isCbdSet) {
            filterCounter++
            _filterCount.value = filterCounter
            isCbdSet = true
        }
    }

    fun setPriceFilter(min: Float, max: Float) {
        priceMax = max
        priceMin = min
        if (!isPriceSet) {
            filterCounter++
            _filterCount.value = filterCounter
            isPriceSet = true
        }
    }

    fun setKeywordFilter(keyword: String) {
        this.keyword = keyword
    }

    fun incrementFilterCounter() {
        filterCounter++
        _filterCount.value = filterCounter
    }

    fun decreaseFilterCounter() {
        filterCounter--
        _filterCount.value = filterCounter
    }

    fun clearFilters() {
        thcMin = THC_MIN
        thcMax = THC_MAX
        cbdMin = CBD_MIN
        cbdMax = CBD_MAX
        priceMin = PRICE_MIN
        priceMax = PRICE_MAX
        ratingMin = 0
        isThcSet = false
        isCbdSet = false
        isPriceSet = false
        categories.clear()
        feelings.clear()
        activities.clear()
        weights.clear()
        brands.clear()
        kinds.clear()
        specialIds = null
        sort = ""
        keyword = null
        filterCounter = 0
        _filterCount.value = filterCounter
        _hasPendingFilterChanges.value = false

    }

    fun loadProducts() {

        return

        viewModelScope.launch {
            val filters: HashMap<String, String> = HashMap()
            val rangeFilters: HashMap<String, String> = HashMap()
            when (val response =
                productInteractor.getProducts("1", "50", null, filters, rangeFilters, null)) {
                is DomainResponse.Success -> {
                    val data = response.value.data
                    _products.value = data.products?.map { it.attributes } ?: listOf()
                    productFacets = data.facets
                }

                is DomainResponse.Error -> {
                    println(response.error)
                }
            }
        }
    }

    fun loadPromotions() {
        viewModelScope.launch {
            when (val response = productInteractor.getPromotions()) {
                is DomainResponse.Success -> {
                    _promotions.value = response.value
                }

                is DomainResponse.Error -> {
                    _uiState.value = UiState.Error(GenericError(response.error.message))
                }
            }
        }
    }

    fun loadHomePageProducts(forceReload: Boolean = false) {
        if (!forceReload && homeProductsLoaded) return
        homeProductsLoaded = false

        viewModelScope.launch {
            when (val response =
                productInteractor.getPublicHomeProducts(rootTypes.joinToString(","))) {
                is DomainResponse.Success -> {
                    val products = response.value.data
                    homeProductsLoaded = true
                    _uiState.value = UiState.Success
                    val categoryAndProductsMap = products.data.products?.groupBy({
                        it.attributes.category
                    }, {
                        it.attributes
                    }) ?: mutableMapOf()

                    _categoriesAndProducts.value = categoryAndProductsMap
                }

                is DomainResponse.Error -> {
                    _uiState.value = UiState.Error(GenericError(response.error.message))
                }
            }
        }
    }
}