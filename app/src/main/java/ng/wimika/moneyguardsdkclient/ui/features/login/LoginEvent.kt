package ng.wimika.moneyguardsdkclient.ui.features.login

import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation
import android.content.Context

sealed class LoginEvent {
    data object OnLoginClick : LoginEvent()
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data object OnPasswordVisibilityToggle : LoginEvent()
    data class UpdateGeoLocation(val geoLocation: GeoLocation) : LoginEvent()
    data class ContinueLoginWithFlaggedLocation(val token: String): LoginEvent()
    data object DismissDangerousLocationModal : LoginEvent()
    data class VerifyIdentity(val token: String, val context: Context): LoginEvent()
    data object ShowDisplayOverAppModal : LoginEvent()
    data object DismissDisplayOverAppModal : LoginEvent()
    data object OpenDisplayOverAppSettings : LoginEvent()
    data class StoreTokenForVerification(val token: String): LoginEvent()
    data object ClearStoredToken : LoginEvent()
    data class EmitNavigateToVerification(val token: String): LoginEvent()
} 