package ng.wimika.moneyguardsdkclient.ui.features.claims.claim_detail

import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimResponse

data class ClaimDetailsState(
    val claim: ClaimResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)



sealed class ClaimDetailsEvent {
    data class LoadClaim(val claimId: Int) : ClaimDetailsEvent()
}