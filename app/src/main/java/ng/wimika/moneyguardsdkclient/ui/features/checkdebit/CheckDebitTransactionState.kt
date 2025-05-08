package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation

data class AlertData(
    val showAlert: Boolean = false,
    val title: String = "",
    val message: String = "",
    val buttonText: String = "Proceed",
    val secondaryButtonText: String? = null
)

data class CheckDebitTransactionState(
    val isLoading: Boolean = false,
    val enableButton: Boolean = false,
    val geoLocation: GeoLocation = GeoLocation(),
    val sourceAccountNumber: String = "",
    val destinationAccountNumber: String = "",
    val destinationBank: String = "",
    val memo: String = "",
    val amount: Double = 0.0,
    val alertData: AlertData = AlertData()
)
