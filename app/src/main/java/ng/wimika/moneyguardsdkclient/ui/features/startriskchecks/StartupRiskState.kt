package ng.wimika.moneyguardsdkclient.ui.features.startriskchecks

import ng.wimika.moneyguard_sdk_commons.types.SpecificRisk

data class StartupRiskState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val riskStatusMessage: String? = null,
    //val isRiskFree: Boolean = false,
    //val isWarningRisk: Boolean = false,
    //val showRiskModal: Boolean = false,
    //val currentRiskEvent: StartupRiskResultEvent? = null
) {
    val shouldEnableButton: Boolean
        get() { return  !isLoading  }
}

sealed class StartupRiskResultEvent {
    object RiskFree : StartupRiskResultEvent()
    data class WarningRisk(val issues: List<SpecificRisk>) : StartupRiskResultEvent()
    data class SevereRisk(val issues: List<SpecificRisk>) : StartupRiskResultEvent()
}