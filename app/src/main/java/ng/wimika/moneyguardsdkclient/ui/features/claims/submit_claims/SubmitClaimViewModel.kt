package ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ng.wimika.moneyguard_sdk.services.claims.MoneyGuardClaim
import ng.wimika.moneyguard_sdk.services.claims.models.Claim
import ng.wimika.moneyguard_sdk.services.policy.MoneyGuardPolicy
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.utils.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.sql.Date

class SubmitClaimViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubmitClaimViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubmitClaimViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SubmitClaimViewModel(private val context: Context) : ViewModel() {
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
            loadIncidentNames()
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
                    nameOfIncident = currentState.nameofIncident ?: "",
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

        val isInputValid = currentState.nameofIncident?.isNotBlank() == true &&
                currentState.lossAmount > 0.0 &&
                currentState.statement.isNotBlank() &&
                currentState.lossDate != null &&
                currentState.selectedAccount != null

        _submitClaimState.update { state ->
            state.copy(shouldEnableButton = isInputValid)
        }
    }

    private fun convertToMultipartBodyParts(files: List<Uri>): List<MultipartBody.Part> {
        return files.mapNotNull { uri ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = FileUtils.getFileName(context, uri)
                val mimeType = FileUtils.getMimeType(context, uri) ?: "application/octet-stream"

                val requestBody = inputStream?.let {
                    object : RequestBody() {
                        override fun contentType() = mimeType.toMediaTypeOrNull()
                        override fun contentLength() = -1L
                        override fun writeTo(sink: BufferedSink) {
                            sink.writeAll(it.source())
                        }
                    }
                } ?: return@mapNotNull null

                MultipartBody.Part.createFormData("attachments", fileName, requestBody)
            } catch (e: Exception) {
                Log.e("ClaimsApiService", "Error creating MultipartBody.Part for URI: $uri", e)
                null
            }
        }
    }

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

    private fun loadIncidentNames() {
        val token = preferenceManager?.getMoneyGuardToken() ?: ""

        viewModelScope.launch {
            moneyGuardClaim?.getIncidentNames(
                token,
                onSuccess = { names ->
                    _submitClaimState.update { currentState ->
                        currentState.copy(incidentNames = names)
                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
    }

    private fun submitClaim(claim: Claim, attachments: List<Uri>) {

        viewModelScope.launch {
            _submitClaimState.update { currentState ->
                currentState.copy(isLoading = true)
            }

            val multipartAttachments = withContext (Dispatchers.Default) {
                convertToMultipartBodyParts(attachments)
            }

            try {
                moneyGuardClaim?.submitClaim(
                    sessionToken = preferenceManager?.getMoneyGuardToken() ?: "",
                    claim = claim,
                    attachments = multipartAttachments,
                    onSuccess = { response ->
                        _submitClaimState.update { 
                            SubmitClaimState(isSuccessful = true)
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
}