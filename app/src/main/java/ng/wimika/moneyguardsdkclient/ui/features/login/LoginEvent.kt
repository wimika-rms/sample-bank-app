package ng.wimika.moneyguardsdkclient.ui.features.login

import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation

sealed class LoginEvent {
    data object OnLoginClick : LoginEvent()
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data object OnPasswordVisibilityToggle : LoginEvent()
    data class UpdateGeoLocation(val geoLocation: GeoLocation) : LoginEvent()
    data class ContinueLoginWithFlaggedLocation(val token: String): LoginEvent()
    data object DismissDangerousLocationModal : LoginEvent()
    data class VerifyIdentity(val token: String): LoginEvent()
} 