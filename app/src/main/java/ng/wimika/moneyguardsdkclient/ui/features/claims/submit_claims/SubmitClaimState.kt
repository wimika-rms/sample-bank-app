package ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims

import java.io.File
import java.util.Date


data class SubmitClaimState(
    val nameofIncident: String = "",
    val lossAmount: Double = 0.0,
    val lossDate: Date? = null,
    val statement: String = "",
    val selectedFiles: List<File> = emptyList(),
    val showDatePicker: Boolean = false,
    val showPermissionRationale: Boolean = false,
    val shouldEnableButton: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


sealed class SubmitClaimEvent {

    data object SubmitClaim : SubmitClaimEvent()

    data class NameOfIncidentChanged(val value: String) : SubmitClaimEvent()
    data class LossAmountChanged(val value: Double) : SubmitClaimEvent()
    data class LossDateChanged(val value: Date) : SubmitClaimEvent()
    data class StatementChanged(val value: String) : SubmitClaimEvent()
    data class OnFilesSelected(val files: List<File>) : SubmitClaimEvent()
    object ShowDatePicker : SubmitClaimEvent()
    object HideDatePicker : SubmitClaimEvent()
    object ShowPermissionRationale : SubmitClaimEvent()
    object HidePermissionRationale : SubmitClaimEvent()

}