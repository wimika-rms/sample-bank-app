package ng.wimika.moneyguardsdkclient.ui.features.startriskchecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguard_sdk.services.prelaunch.MoneyGuardPrelaunch
import ng.wimika.moneyguard_sdk.services.prelaunch.types.PreLaunchDecision
import ng.wimika.moneyguard_sdk_commons.types.RiskStatus
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp


sealed class StartupRiskEvent {
    data object StartStartUpRiskCheck: StartupRiskEvent()
    data object ProceedToLogin: StartupRiskEvent()
}

class StartupRiskViewModel: ViewModel() {

    private val moneyGuardPrelaunch: MoneyGuardPrelaunch? by lazy {
        MoneyGuardClientApp.sdkService?.prelaunch()
    }

    private val _startupRiskState: MutableStateFlow<StartupRiskState> = MutableStateFlow(StartupRiskState())
    val startupRiskState: StateFlow<StartupRiskState> = _startupRiskState
        .onStart {
            checkStartupRisks()
        }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(1000),
            StartupRiskState()
        )

    private val _uiEvent = MutableSharedFlow<StartupRiskResultEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: StartupRiskEvent) {
        when (event) {
            StartupRiskEvent.StartStartUpRiskCheck -> {
                //checkStartupRisks()
            }
            StartupRiskEvent.ProceedToLogin -> {
                // Navigation is handled by the screen
            }
        }
    }

    private fun checkStartupRisks() {
        viewModelScope.launch {
            _startupRiskState.update { it.copy(isLoading = true) }
            
            try {
                val startupRisk = moneyGuardPrelaunch?.startup()
                _startupRiskState.update { 
                    it.copy(
                        isLoading = false,
                        risks = startupRisk?.risks ?: emptyList()
                    )
                }

                if (startupRisk == null || startupRisk.risks.isEmpty()) {
                    _uiEvent.emit(StartupRiskResultEvent.RiskFree)
                } else {
                    val severeRisks = startupRisk.risks.filter { it.status == RiskStatus.RISK_STATUS_UNSAFE }
                    val warningRisks = startupRisk.risks.filter { it.status == RiskStatus.RISK_STATUS_WARN }

                    if (severeRisks.isNotEmpty()) {
                        _uiEvent.emit(StartupRiskResultEvent.SevereRisk(severeRisks))
                    }
                    if (warningRisks.isNotEmpty()) {
                        _uiEvent.emit(StartupRiskResultEvent.WarningRisk(warningRisks))
                    }
                }
            } catch (e: Exception) {
                _startupRiskState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun dismissRiskModal() {
//        _startupRiskState.update { currentState ->
//            currentState.copy(showRiskModal = false)
//        }
    }
}