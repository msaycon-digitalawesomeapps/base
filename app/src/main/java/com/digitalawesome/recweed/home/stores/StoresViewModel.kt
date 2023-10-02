package com.digitalawesome.recweed.home.stores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalawesome.dispensary.components.models.UiState
import com.digitalawesome.dispensary.domain.application.DomainResponse
import com.digitalawesome.dispensary.domain.interactors.ProductInteractor
import com.digitalawesome.dispensary.domain.interactors.StoreInteractor
import com.digitalawesome.dispensary.domain.models.StoreModel
import com.digitalawesome.dispensary.domain.models.StoreResponse
import kotlinx.coroutines.launch

class StoresViewModel(private val storeInteractor: StoreInteractor, private val productInteractor: ProductInteractor) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _stores = MutableLiveData<StoreResponse>()
    val stores: LiveData<StoreResponse> = _stores

    private val _selectedStore = MutableLiveData<StoreModel?>()
    val selectedStore: LiveData<StoreModel?> = _selectedStore

    var savedSelectedStore: StoreModel? = null
        private set
        get() {
            return (field ?: storeInteractor.getSelectedStore()).also {
                _selectedStore.value = it
            }
        }

    fun loadStores() {
        viewModelScope.launch {
            val response = storeInteractor.getStores()

            when(response) {
                is DomainResponse.Success -> {
                    val stores = response.value
                    _stores.value = stores
                }
                is DomainResponse.Error -> {

                }
            }
        }

    }

    fun selectStore(storeModel: StoreModel) {
        storeInteractor.selectStore(storeModel)
        _selectedStore.value = storeModel
    }
}