package ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguard_sdk.services.claims.MoneyGuardClaim
import ng.wimika.moneyguard_sdk.services.claims.models.Claim
import ng.wimika.moneyguard_sdk.services.policy.MoneyGuardPolicy
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.sql.Date

class SubmitViewModel : ViewModel() {
    private val moneyGuardClaim: MoneyGuardClaim? by lazy {
        MoneyGuardClientApp.sdkService?.claim()
    }

    private val moneyGuardPolicy: MoneyGuardPolicy? by lazy {
        MoneyGuardClientApp.sdkService?.policy()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val _submitClaimState: MutableStateFlow<SubmitClaimState> =
        MutableStateFlow(SubmitClaimState())
    val submitClaimState: StateFlow<SubmitClaimState> = _submitClaimState
        .onStart {
            loadPolicyAccounts()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SubmitClaimState(),
        )

    fun onEvent(event: SubmitClaimEvent) {
        when (event) {
            SubmitClaimEvent.HideDatePicker -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(showDatePicker = false)
                }
            }

            is SubmitClaimEvent.LossAmountChanged -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(
                        lossAmount = event.value,
                    )
                }
            }

            is SubmitClaimEvent.LossDateChanged -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(
                        lossDate = event.value,
                        showDatePicker = false,
                    )
                }
            }

            is SubmitClaimEvent.NameOfIncidentChanged -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(
                        nameofIncident = event.value,
                    )
                }
            }

            is SubmitClaimEvent.OnFilesSelected -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(selectedFiles = event.files)
                }
            }

            SubmitClaimEvent.ShowDatePicker -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(showDatePicker = true)
                }
            }

            SubmitClaimEvent.ShowPermissionRationale -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(showPermissionRationale = true)
                }
            }

            SubmitClaimEvent.HidePermissionRationale -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(showPermissionRationale = false)
                }
            }

            is SubmitClaimEvent.StatementChanged -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(
                        statement = event.value
                    )
                }
            }

            SubmitClaimEvent.SubmitClaim -> {
                val currentState = _submitClaimState.value
                val claim = Claim(
                    nameOfIncident = currentState.nameofIncident,
                    lossAmount = currentState.lossAmount,
                    lossDate = currentState.lossDate ?: Date(System.currentTimeMillis()),
                    statement = currentState.statement,
                    accountId = currentState.selectedAccount?.id ?: -1L
                )
                submitClaim(claim, currentState.selectedFiles)
            }

            is SubmitClaimEvent.AccountSelected -> {
                _submitClaimState.update { currentState ->
                    currentState.copy(
                        selectedAccount = event.account
                    )
                }
            }
        }
        validateForm()
    }

    private fun validateForm() {
        val currentState = _submitClaimState.value

        val isInputValid = currentState.nameofIncident.isNotBlank() &&
                currentState.lossAmount > 0.0 &&
                currentState.statement.isNotBlank() &&
                currentState.lossDate != null &&
                currentState.selectedAccount != null

        _submitClaimState.update { state ->
            state.copy(shouldEnableButton = isInputValid)
        }
    }

    private fun convertToMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        return files.map { file ->
            val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("attachments", file.name, requestBody)
        }
    }

    /*private fun convertToMultipartBodyParts(files: List<File>): List<MultipartBody.Part> {
        return files.mapNotNull { file ->
            try {
                if (!file.exists()) {
                    Log.e("ClaimsApiService", "File does not exist: ${file.absolutePath}")
                    return@mapNotNull null
                }

                val mediaType = when (file.extension.lowercase()) {
                    "pdf" -> "application/pdf"
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    "doc", "docx" -> "application/msword"
                    else -> "application/octet-stream"
                }

                val requestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("attachments", file.name, requestBody)
            } catch (e: Exception) {
                Log.e("ClaimsApiService", "Error creating MultipartBody.Part for file: ${file.absolutePath}", e)
                null
            }
        }
    }*/


    private fun loadPolicyAccounts() {
        val token = preferenceManager?.getMoneyGuardToken() ?: ""

        viewModelScope.launch {
            moneyGuardPolicy?.getUserAccounts(token, partnerBankId = 101)?.fold(
                onSuccess = { response ->
                    _submitClaimState.update { currentState ->
                        currentState.copy(accounts = response.bankAccounts)
                    }
                },
                onFailure = { exception ->
                    //swallow the error for now.
                    exception.printStackTrace()
                }
            )
        }
    }


    private fun submitClaim(claim: Claim, attachments: List<File>) {
        _submitClaimState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        try {
            val multipartAttachments = convertToMultipartBodyParts(attachments)
            moneyGuardClaim?.submitClaim(
                sessionToken = preferenceManager?.getMoneyGuardToken() ?: "",
                claim = claim,
                attachments = multipartAttachments,
                onSuccess = { response ->
                    _submitClaimState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _submitClaimState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        } catch (e: Exception) {
            _submitClaimState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }
}