package com.digitalawesome.clearchoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalawesome.dispensary.domain.PER_PAGE
import com.digitalawesome.dispensary.domain.REWARDS_DESCRIPTION
import com.digitalawesome.dispensary.domain.UNVERIFIED_EMAIL
import com.digitalawesome.dispensary.domain.application.DomainResponse
import com.digitalawesome.dispensary.domain.interactors.AppInteractor
import com.digitalawesome.dispensary.domain.interactors.AuthInteractor
import com.digitalawesome.dispensary.domain.interactors.UserInteractor
import com.digitalawesome.dispensary.domain.models.AchievementResponse
import com.digitalawesome.dispensary.domain.models.AchievementsModel
import com.digitalawesome.dispensary.domain.models.AuthResponse
import com.digitalawesome.dispensary.domain.models.CartModel
import com.digitalawesome.dispensary.domain.models.LoginResponse
import com.digitalawesome.dispensary.domain.models.NotificationsResponse
import com.digitalawesome.dispensary.domain.models.OrdersModel
import com.digitalawesome.dispensary.domain.models.SettingsResponse
import com.digitalawesome.dispensary.domain.models.UserModel
import com.digitalawesome.dispensary.domain.services.SharedPrefsService
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class UserViewModel(
    private val appInteractor: AppInteractor,
    private val authInteractor: AuthInteractor,
    private val userInteractor: UserInteractor,
    private val sharedPrefsService: SharedPrefsService,
) : ViewModel() {

    private var userId: Int = 0
    private var loggedInEmail: String? = null

    private var pendingEmail: String? = null
    private var pendingPassword: String? = null
    private var deviceId: String? = null
    private var firebaseToken: String? = null
    private var intercomKey: String? = null

    private val _userData: MutableLiveData<UserModel?> = MutableLiveData()
    val userData: LiveData<UserModel?> = _userData

    private val _orders: MutableLiveData<List<OrdersModel>> = MutableLiveData()
    val orders: LiveData<List<OrdersModel>> = _orders

    private val _cart: MutableLiveData<List<CartModel>> = MutableLiveData()
    val cart: LiveData<List<CartModel>> = _cart

    private val _likedProducts: MutableLiveData<Map<String, Map<String, Int>>?> = MutableLiveData()
    val likedProducts: LiveData<Map<String, Map<String, Int>>?> = _likedProducts

    private val _text = MutableLiveData<String>().apply {
        value = "Account Page"
    }
    val text: LiveData<String> = _text

    private var activeAchievement: AchievementsModel? = null
    fun getActiveAchievement(): AchievementsModel? {
        return activeAchievement
    }

    fun setActiveAchievement(achievement: AchievementsModel) {
        activeAchievement = achievement
    }

    private var activeOrder: OrdersModel? = null
    fun getActiveOrder(): OrdersModel? {
        return activeOrder
    }

    fun setActiveOrder(order: OrdersModel) {
        activeOrder = order
    }

    fun isAgeVerified(): Boolean = appInteractor.ageVerified()

    fun markAsRead(notificationId: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = userInteractor.markNotificationAsRead(notificationId)) {
                is DomainResponse.Success -> callback(true, null)
                is DomainResponse.Error -> callback(false, response.error.message)
            }
        }
    }

    fun getNotifications(page: String, callback: (NotificationsResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = userInteractor.getNotifications(page, PER_PAGE.toString())) {
                is DomainResponse.Success -> callback(response.value, null)
                is DomainResponse.Error -> callback(null, response.error.message)
            }
        }
    }

    fun getSingleAchievements(
        userId: Int, achievementId: Int, callback: (AchievementResponse?, String?) -> Unit
    ) {
        viewModelScope.launch {
            when (val response = userInteractor.getSingleAchievement(userId, achievementId)) {
                is DomainResponse.Success -> callback(response.value, null)
                is DomainResponse.Error -> callback(null, response.error.message)
            }
        }
    }

    fun getOrders(page: Int) {
        viewModelScope.launch {
            when (val response = userInteractor.getOrders(page, 10, "stores")) {
                is DomainResponse.Success -> {
                    _orders.value = response.value ?: listOf()
                }
                is DomainResponse.Error -> {
                }
            }
        }
    }

    fun getOrderById(orderId: Int) {
        viewModelScope.launch {
            when (val response = userInteractor.getOrderById(orderId)) {
                is DomainResponse.Success -> {

                }
                is DomainResponse.Error -> {

                }
            }
        }
    }

    fun getServerSettings(callback: (SettingsResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = appInteractor.getSettings()) {
                is DomainResponse.Success -> {
                    if (!response.value.data?.rewardsDescription.isNullOrEmpty()) {
                        sharedPrefsService.putString(
                            REWARDS_DESCRIPTION,
                            response.value.data?.rewardsDescription
                        )
                    }
                    callback(response.value, null)
                }
                is DomainResponse.Error -> callback(null, response.error.message)
            }
        }
    }

    fun getPublicToken(callback: (LoginResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response =
                appInteractor.getPublicToken(BuildConfig.PUBLIC_CRED, BuildConfig.PUBLIC_KEY)) {
                is DomainResponse.Success -> callback(response.value, null)
                is DomainResponse.Error -> callback(null, response.error.message)
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val response = userInteractor.getCurrentUser()
            when (response) {
                is DomainResponse.Success -> {
                    val userData = response.value.data ?: return@launch
                    val email = userData.attributes.email
                    if (email != null && !intercomKey.isNullOrEmpty()) {
                        val registration =
                            Registration
                                .create()
                                .withEmail(email)
                                .withUserId(userData.id.toString())
                        Intercom
                            .client()
                            .setUserHash(createIntercomHash(intercomKey!!, email))

                        Intercom
                            .client()
                            .loginIdentifiedUser(registration)
                    }
                    loggedInEmail =
                        if (email != BuildConfig.PUBLIC_CRED) {
                            email
                        } else null

                    userId = if (userData.attributes.email != BuildConfig.PUBLIC_CRED) {
                        userData.id ?: 0
                    } else 0

                    _userData.value = userData.copy(
                        id = userId,
                        attributes = userData.attributes.copy(
                            email = loggedInEmail
                        )
                    )
                }
                is DomainResponse.Error -> {
                }
            }
        }
    }

    fun likeDislikeOrderItem(productId: String, like: Boolean, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val response = if (like) {
                userInteractor.likeOrderItem(productId)
            } else {
                userInteractor.dislikeOrderItem(productId)
            }

            when (response) {
                is DomainResponse.Success -> {
                    val userData = response.value.data ?: return@launch
                    _userData.value = userData.copy(
                        id = userId,
                        attributes = userData.attributes.copy(
                            email = loggedInEmail
                        )
                    )
                    updateLikedProducts(userData)
                    callback(true, null)
                }

                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun updatePassword(
        oldPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val response = userInteractor.updatePassword(oldPassword, newPassword, oldPassword)
            when (response) {
                is DomainResponse.Success -> {
                    val userData = response.value.data ?: return@launch
                    _userData.value = userData.copy(
                        id = userId,
                        attributes = userData.attributes.copy(
                            email = loggedInEmail
                        )
                    )
                    callback(true, null)
                }

                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun updateProfile(
        firstName: String?,
        lastName: String?,
        email: String?,
        phone: String?,
        state: String,
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val response = userInteractor.updateProfile(firstName, lastName, email, phone, state)
            when (response) {
                is DomainResponse.Success -> {
                    val userData = response.value.data ?: return@launch
                    val email = userData.attributes.email

                    if (email != null && userData.attributes.phone != null) {
                        loggedInEmail = email
                        userId = userData.id ?: 0
                    }

                    _userData.value = userData.copy(
                        id = userId,
                        attributes = userData.attributes.copy(
                            email = loggedInEmail
                        )
                    )
                    callback(true, null)
                }

                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    fun register(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        password: String,
        code: String?,
        optInNewsletter: Boolean,
        optInTextDeals: Boolean,
        selectedState: String,
        callback: (AuthResponse?, String?) -> Unit
    ) {
        viewModelScope.launch {
            val response = authInteractor.register(
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                email = email,
                password = password,
                passwordConfirmation = password,
                code = code,
                optInEmail = optInNewsletter.toInt(),
                optInText = optInTextDeals.toInt(),
                state = selectedState
            )

            when (response) {
                is DomainResponse.Success -> {
                    _userData.value = response.value.data
                    pendingEmail = email
                    pendingPassword = password
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun login(email: String, password: String, callback: (LoginResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = authInteractor.login(email, password)) {
                is DomainResponse.Success -> {
                    clearPendingData()
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun resendVerificationCode(email: String, callback: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = authInteractor.resend(email)) {
                is DomainResponse.Success -> {
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun forgotPassword(email: String, callback: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = authInteractor.forgotPassword(email)) {
                is DomainResponse.Success -> {
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun forgotPasswordResend(email: String, callback: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = authInteractor.resend(email)) {
                is DomainResponse.Success -> {
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun forgotPasswordVerify(
        email: String,
        code: String,
        password: String,
        callback: (AuthResponse?, String?) -> Unit
    ) {
        viewModelScope.launch {
            when (val response = authInteractor.forgotPasswordVerify(email, code, password, password)) {
                is DomainResponse.Success -> {
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }


    fun verifyCode(email: String, code: String, callback: (AuthResponse?, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = authInteractor.verify(email, code)) {
                is DomainResponse.Success -> {
                    callback(response.value, null)
                }
                is DomainResponse.Error -> {
                    callback(null, response.error.message)
                }
            }
        }
    }

    fun deleteAccount(callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            when (val response = userInteractor.deleteAccount()) {
                is DomainResponse.Success -> {
                    callback(true, null)
                }
                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun uploadAvatar(file: File, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val imageFileForm =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imgPart = MultipartBody.Part.createFormData(
                "avatar", file.absolutePath, imageFileForm
            )

            when (val response = userInteractor.uploadAvatar(imgPart)) {
                is DomainResponse.Success -> {
                    val userData = response.value.data ?: return@launch
                    _userData.value = userData
                    callback(true, null)
                }
                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun sendFCMToken(callback: (Boolean, String?) -> Unit) {
        val firebaseToken = firebaseToken
        val deviceId = deviceId
        if (firebaseToken == null || deviceId == null) {
            callback(false, "Either DeviceID or FirebaseToken is null")
            return
        }
        viewModelScope.launch {
            when (val response = userInteractor.sendFCMToken(deviceId, firebaseToken)) {
                is DomainResponse.Success -> {
                    callback(true, null)
                }
                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun patchNotifications(isEnabled: Boolean, callback: (Boolean, String?) -> Unit) {
        val firebaseToken = firebaseToken
        val deviceId = deviceId
        if (firebaseToken == null || deviceId == null) {
            callback(false, "Either DeviceID or FirebaseToken is null")
            return
        }
        viewModelScope.launch {
            when (val response = userInteractor.toggleNotifications(deviceId, isEnabled.toInt())) {
                is DomainResponse.Success -> {
                    callback(true, null)
                }
                is DomainResponse.Error -> {
                    callback(false, response.error.message)
                }
            }
        }
    }

    fun logAppReview(result: (Boolean, String?) -> Unit) {

    }


    fun logBlogRead(blogId: Int, result: (Boolean, String?) -> Unit) {

    }

    private fun updateLikedProducts(userData: UserModel?) {
        if (!userData?.attributes?.likedProducts.isNullOrEmpty()) {
            _likedProducts.value = userData?.attributes?.likedProducts

            userData?.attributes?.likedProducts?.forEach { storeProducts ->
                storeProducts.value.forEach { productId, liked -> }
            }
        }
    }

    private fun createIntercomHash(secret: String, message: String): String {
        val algorithm = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        algorithm.init(secretKey)

        val hash = algorithm.doFinal(message.toByteArray())
        val result = StringBuffer()
        for (b in hash) {
            result.append(String.format("%02x", b))
        }
        return result.toString()
    }

    private fun clearPendingData() {
        sharedPrefsService.remove(UNVERIFIED_EMAIL)
        pendingEmail = null
        pendingPassword = null
    }

    fun logout() {
        _userData.value = null
        loggedInEmail = null
        userId = 0
    }

    fun getCart() {
        viewModelScope.launch {
            when (val response = userInteractor.getCart()) {
                is DomainResponse.Success -> {
                    _cart.value = response.value!!
                }
                is DomainResponse.Error -> {
                }
            }
        }
    }

    fun addToFavorites(productId: String) {
        viewModelScope.launch {
            userInteractor.addToFavorite(productId)
        }
    }

    fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            userInteractor.removeFromFavorite(productId)
        }
    }

    fun addToCart(productId: String, variantId: String, quantity: Int) {
        viewModelScope.launch {

            when (val response = userInteractor.addToCart(productId, variantId, quantity)) {
                is DomainResponse.Success -> {
                    _cart.value = response.value!!
                }
                is DomainResponse.Error -> {
                }
            }
        }
    }
}