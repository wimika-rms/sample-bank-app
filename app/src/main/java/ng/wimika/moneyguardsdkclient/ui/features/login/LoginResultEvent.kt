package ng.wimika.moneyguardsdkclient.ui.features.login

import ng.wimika.moneyguard_sdk_commons.types.RiskStatus


sealed class LoginResultEvent {
    data class CredentialCheckSuccessful(val result: RiskStatus): LoginResultEvent()
    data class LoginSuccessful(val token: String): LoginResultEvent()
    data class LoginFailed(val error: String): LoginResultEvent()
    data class NavigateToVerification(val token: String): LoginResultEvent()
    data object NavigateToLanding: LoginResultEvent()
}