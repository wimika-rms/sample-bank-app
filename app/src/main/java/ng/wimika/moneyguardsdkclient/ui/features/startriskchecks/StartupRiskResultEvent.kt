package ng.wimika.moneyguardsdkclient.ui.features.startriskchecks

import ng.wimika.moneyguard_sdk_commons.types.SpecificRisk

sealed class StartupRiskResultEvent {
    data object RiskFree: StartupRiskResultEvent()
    data class SevereRisk(val issues: List<SpecificRisk>): StartupRiskResultEvent()
    data class WarningRisk(val issues: List<SpecificRisk>): StartupRiskResultEvent()
}