package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation

data class CheckDebitTransactionState(
    val isLoading: Boolean = false,
    val enableButton: Boolean = false,
    val geoLocation: GeoLocation = GeoLocation(),
    val sourceAccountNumber: String = "",
    val destinationAccountNumber: String = "",
    val destinationBank: String = "",
    val memo: String = "",
    val amount: Double = 0.0
)
