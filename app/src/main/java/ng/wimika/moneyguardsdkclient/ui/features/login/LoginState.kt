package ng.wimika.moneyguardsdkclient.ui.features.login

import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation

data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val sessionId: String? = null,
    val geoLocation: GeoLocation? = null,
    val showDangerousLocationModal: Boolean = false,
    val token: String? = null,
    val showDisplayOverAppModal: Boolean = false,
)
