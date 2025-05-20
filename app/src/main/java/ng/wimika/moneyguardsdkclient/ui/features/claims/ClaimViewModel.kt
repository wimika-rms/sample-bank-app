package ng.wimika.moneyguardsdkclient.ui.features.claims

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ng.wimika.moneyguard_sdk.services.claims.MoneyGuardClaim
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import java.util.Date

class ClaimViewModel : ViewModel() {

    private val moneyGuardClaim: MoneyGuardClaim? by lazy {
        MoneyGuardClientApp.sdkService?.claim()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val _claimState: MutableStateFlow<ClaimListState> = MutableStateFlow(ClaimListState())
    val claimState: StateFlow<ClaimListState> = _claimState
        .onStart {
            getClaims()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClaimListState())


    fun onEvent(event: ClaimEvent) {
        when (event) {
            is ClaimEvent.OnFilterSelected -> {
                _claimState.update { currentState ->
                    currentState.copy(status = event.claimStatus, errorMessage = null)
                }

                getClaims()
            }
        }
    }

    private fun getClaims() {
        _claimState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        try {
            moneyGuardClaim?.getClaims(
                preferenceManager?.getMoneyGuardToken() ?: "",
                from = Date(System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000), // 1year ago
                to = Date(System.currentTimeMillis()),
                bank = "",
                claimStatus = _claimState.value.status,
                onSuccess = { response ->
                    _claimState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            claims = response,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = {
                    _claimState.update { currentState ->
                        currentState.copy(isLoading = false, errorMessage = it.message)
                    }
                }
            )
        } catch (e: Exception) {
            _claimState.update { currentState ->
                currentState.copy(isLoading = false, errorMessage = e.message)
            }
        }

    }
}