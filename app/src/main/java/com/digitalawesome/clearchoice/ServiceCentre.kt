package com.digitalawesome.clearchoice

import com.digitalawesome.dispensary.domain.application.ServiceCentreInterface
import com.digitalawesome.dispensary.domain.application.ServiceInterface
import com.digitalawesome.dispensary.domain.services.APIService
import com.digitalawesome.dispensary.domain.services.SharedPrefsService


class ServiceCentre(
    // services here
    private val apiService: APIService,
    private val sharedPrefsService: SharedPrefsService
): ServiceCentreInterface {
    override fun services(): List<ServiceInterface> =
        listOf(
            apiService,
            sharedPrefsService
        )
}