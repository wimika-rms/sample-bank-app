package ng.wimika.moneyguardsdkclient.ui.features.claims

import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimResponse
import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimStatus

data class ClaimListState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val claims: List<ClaimResponse> = listOf(),
    val status: ClaimStatus = ClaimStatus.Submitted
)


sealed class ClaimEvent {
    data class OnFilterSelected(val claimStatus: ClaimStatus) : ClaimEvent()
}