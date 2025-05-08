package ng.wimika.moneyguardsdkclient.ui.features.login

import ng.wimika.moneyguard_sdk_commons.types.RiskStatus


sealed class LoginResultEvent {

    data object LoginSuccessful: LoginResultEvent()
    data class CredentialCheckSuccessful(val result: RiskStatus): LoginResultEvent()
}