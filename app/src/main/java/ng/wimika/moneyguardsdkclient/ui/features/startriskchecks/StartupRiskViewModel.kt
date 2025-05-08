package ng.wimika.moneyguardsdkclient.ui.features.startriskchecks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguard_sdk.services.prelaunch.MoneyGuardPrelaunch
import ng.wimika.moneyguard_sdk.services.prelaunch.types.PreLaunchDecision
import ng.wimika.moneyguard_sdk_commons.types.RiskStatus
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp

class StartupRiskViewModel: ViewModel() {

    private val moneyGuardPrelaunch: MoneyGuardPrelaunch? by lazy {
        MoneyGuardClientApp.sdkService?.prelaunch()
    }

    private val _startupRiskState: MutableStateFlow<StartupRiskState> = MutableStateFlow(StartupRiskState())
    val startupRiskState: StateFlow<StartupRiskState> = _startupRiskState
        .onStart {
            accessStartupRisks()
        }
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            StartupRiskState()
        )

    fun accessStartupRisks() {
        _startupRiskState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        viewModelScope.launch {
            val startupRisk = moneyGuardPrelaunch?.startup()

            _startupRiskState.update { currentState ->
                currentState.copy(isLoading = false)
            }

            if (startupRisk != null && startupRisk.moneyGuardActive) {
                val issues = startupRisk.risks.filter { risk -> risk.status != RiskStatus.RISK_STATUS_SAFE }

                when(startupRisk.preLaunchVerdict.decision) {
                    PreLaunchDecision.Launch -> {
                        _startupRiskState.update { currentState ->
                            currentState.copy(
                                isRiskFree = true,
                                currentRiskEvent = StartupRiskResultEvent.RiskFree
                            )
                        }
                    }
                    PreLaunchDecision.LaunchWithWarning -> {
                        _startupRiskState.update { currentState ->
                            currentState.copy(
                                isWarningRisk = true,
                                currentRiskEvent = StartupRiskResultEvent.WarningRisk(issues),
                                showRiskModal = true
                            )
                        }
                    }
                    PreLaunchDecision.DoNotLaunch -> {
                        _startupRiskState.update { currentState ->
                            currentState.copy(
                                currentRiskEvent = StartupRiskResultEvent.SevereRisk(issues),
                                showRiskModal = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun dismissRiskModal() {
        _startupRiskState.update { currentState ->
            currentState.copy(showRiskModal = false)
        }
    }
}