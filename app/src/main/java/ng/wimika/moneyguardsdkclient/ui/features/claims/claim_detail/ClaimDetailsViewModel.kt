package ng.wimika.moneyguardsdkclient.ui.features.claims.claim_detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ng.wimika.moneyguard_sdk.services.claims.MoneyGuardClaim
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager

class ClaimDetailsViewModel : ViewModel() {
    private val moneyGuardClaim: MoneyGuardClaim? by lazy {
        MoneyGuardClientApp.sdkService?.claim()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val _claimDetailsState = MutableStateFlow(ClaimDetailsState())
    val claimDetailsState: StateFlow<ClaimDetailsState> = _claimDetailsState
        .asStateFlow()

    fun onEvent(event: ClaimDetailsEvent) {
        when (event) {
            is ClaimDetailsEvent.LoadClaim -> {
                getClaimDetails(event.claimId)
            }
        }
    }

    private fun getClaimDetails(claimId: Int) {
        _claimDetailsState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        try {
            moneyGuardClaim?.getClaim(
                sessionToken = preferenceManager?.getMoneyGuardToken() ?: "",
                claimId = claimId,
                onSuccess = { response ->
                    _claimDetailsState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            claim = response,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _claimDetailsState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        } catch (e: Exception) {
            _claimDetailsState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
}