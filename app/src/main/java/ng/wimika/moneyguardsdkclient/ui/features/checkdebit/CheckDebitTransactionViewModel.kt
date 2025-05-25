package ng.wimika.moneyguardsdkclient.ui.features.checkdebit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ng.wimika.moneyguard_sdk.services.transactioncheck.TransactionCheck
import ng.wimika.moneyguard_sdk.services.transactioncheck.models.DebitTransaction
import ng.wimika.moneyguard_sdk.services.transactioncheck.models.DebitTransactionCheckResult
import ng.wimika.moneyguard_sdk.services.transactioncheck.models.LatLng
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation
import ng.wimika.moneyguard_sdk_commons.types.RiskStatus



class CheckDebitTransactionViewModel: ViewModel() {

    private val _checkDebitState: MutableStateFlow<CheckDebitTransactionState> =
        MutableStateFlow(CheckDebitTransactionState())

    val checkDebitState: StateFlow<CheckDebitTransactionState> = _checkDebitState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CheckDebitTransactionState()
        )

    private val transactionCheck: TransactionCheck? by lazy {
        MoneyGuardClientApp.sdkService?.transactionCheck()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    fun updateLocation(geoLocation: GeoLocation) {
        _checkDebitState.update { currentState ->
            currentState.copy(geoLocation = geoLocation)
        }
    }

    fun onEvent(event: CheckDebitTransactionEvent) {
        when (event) {
            CheckDebitTransactionEvent.CheckDebitClick -> {
                val currentState = _checkDebitState.value
                val transactionData = TransactionData(
                    amount = currentState.amount,
                    sourceAccountNumber = currentState.sourceAccountNumber,
                    destinationAccountNumber = currentState.destinationAccountNumber,
                    destinationBank = currentState.destinationBank,
                    memo = currentState.memo,
                    geoLocation = currentState.geoLocation
                )

                checkDebitTransaction(transactionData)
            }

            CheckDebitTransactionEvent.DismissAlert -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(
                        alertData = AlertData()
                    )
                }
            }

            is CheckDebitTransactionEvent.UpdateSourceAccountNumber -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(sourceAccountNumber = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateDestinationAccountNumber -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(destinationAccountNumber = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateDestinationBank -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(destinationBank = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateMemo -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(memo = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateAmount -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(amount = event.value)
                }
            }
        }

        shouldEnableButton()
    }

    private fun checkDebitTransaction(data: TransactionData) {
        _checkDebitState.update { currentState ->
            currentState.copy(isLoading = true)
        }
        val sessionToken = preferenceManager?.getMoneyGuardToken() ?: ""
        val debitTransaction = DebitTransaction(
            sourceAccountNumber = data.sourceAccountNumber,
            destinationAccountNumber = data.destinationAccountNumber,
            destinationBank = data.destinationBank,
            memo = data.memo,
            amount = data.amount,
            location = LatLng(
                longitude = data.geoLocation.lon,
                latitude = data.geoLocation.lat
            )
        )

        transactionCheck?.checkDebitTransaction(sessionToken, debitTransaction,
            onSuccess = { result ->
                if (result.success) {
                    handleRiskStatus(result)
                }
            },
            onFailure = {
                _checkDebitState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
        )
    }

    private fun handleRiskStatus(result: DebitTransactionCheckResult) {
        when (result.status) {
            RiskStatus.RISK_STATUS_WARN -> {
                val commaSeparatedRisks = result.risks
                    .filter { it.status == RiskStatus.RISK_STATUS_WARN }
                    .joinToString(", ") { it.statusSummary.toString() }
                
                _checkDebitState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        alertData = AlertData(
                            showAlert = true,
                            title = "Warning",
                            message = "We have detected some threats that may put your transaction at risk, " +
                                    "please review and proceed with caution - $commaSeparatedRisks",
                            buttonText = "Proceed"
                        )
                    )
                }
            }
            RiskStatus.RISK_STATUS_UNSAFE_CREDENTIALS -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        alertData = AlertData(
                            showAlert = true,
                            title = "2FA Required",
                            message = "We have detected that you logged in with compromised credentials, " +
                                    "a 2FA is required to proceed",
                            buttonText = "Proceed"
                        )
                    )
                }
            }
            RiskStatus.RISK_STATUS_UNSAFE_LOCATION -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        alertData = AlertData(
                            showAlert = true,
                            title = "2FA Required",
                            message = "We have detected that this transaction is happening in a suspicious location, " +
                                    "a 2FA is required to proceed",
                            buttonText = "Proceed"
                        )
                    )
                }
            }
            RiskStatus.RISK_STATUS_UNSAFE -> {
                val commaSeparatedRisks = result.risks
                    .filter { it.status == RiskStatus.RISK_STATUS_UNSAFE }
                    .joinToString(", ") { it.statusSummary.toString() }
                
                _checkDebitState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        alertData = AlertData(
                            showAlert = true,
                            title = "2FA Required",
                            message = "We have detected some threats that may put your transaction at risk, " +
                                    "a 2FA is required to proceed - $commaSeparatedRisks",
                            buttonText = "Proceed"
                        )
                    )
                }
            }
            else -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
        }
    }

    private fun shouldEnableButton() {
        val currentState = _checkDebitState.value
        val shouldEnableButton = currentState.sourceAccountNumber.isNotEmpty() &&
                currentState.destinationAccountNumber.isNotEmpty() &&
                currentState.destinationBank.isNotEmpty() &&
                currentState.memo.isNotEmpty() && currentState.amount > 0.0 &&
                !currentState.isLoading

        _checkDebitState.update { currentState ->
            currentState.copy(enableButton = shouldEnableButton)
        }
    }
}

data class TransactionData(
    val amount: Double,
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val destinationBank: String,
    val memo: String,
    val geoLocation: GeoLocation
)