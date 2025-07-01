package ng.wimika.moneyguardsdkclient.ui.features.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ng.wimika.moneyguard_sdk.services.authentication.MoneyGuardAuthentication
import ng.wimika.moneyguard_sdk_auth.datasource.auth_service.models.SessionResponse
import ng.wimika.moneyguard_sdk_auth.datasource.auth_service.models.credential.Credential
import ng.wimika.moneyguard_sdk_auth.datasource.auth_service.models.credential.CredentialScanResult
import ng.wimika.moneyguard_sdk_auth.datasource.auth_service.models.credential.HashAlgorithm
import ng.wimika.moneyguard_sdk_commons.types.RiskStatus
import ng.wimika.moneyguard_sdk_commons.types.MoneyGuardResult
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepository
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepositoryImpl
import ng.wimika.moneyguardsdkclient.utils.computeSha256Hash
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardAppStatus
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguard_sdk.services.utility.models.LocationCheck
import ng.wimika.moneyguardsdkclient.ui.features.login.LoginResultEvent
import kotlin.Result


class LoginViewModel : ViewModel() {

    private val loginRepository: LoginRepository = LoginRepositoryImpl()

    private val moneyGuardAuthentication: MoneyGuardAuthentication? by lazy {
        MoneyGuardClientApp.sdkService?.authentication()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val moneyGuardUtility: MoneyGuardUtility? by lazy {
        MoneyGuardClientApp.sdkService?.utility()
    }

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _loginResultEvent: MutableSharedFlow<LoginResultEvent> = MutableSharedFlow()
    val loginResultEvent: SharedFlow<LoginResultEvent> = _loginResultEvent.asSharedFlow()

    private val _showDangerousLocationModal = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val showDangerousLocationModal: StateFlow<Pair<Boolean, String?>> = _showDangerousLocationModal.asStateFlow()

    private val _showDisplayOverAppModal = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val showDisplayOverAppModal: StateFlow<Pair<Boolean, String?>> = _showDisplayOverAppModal.asStateFlow()

    companion object {
        private const val WIMIKA_BANK = 101
    }

    fun logOut() {
        viewModelScope.launch {
            moneyGuardAuthentication?.logout()
            preferenceManager?.clear()
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> {
                onEmailChange(event.email)
            }

            is LoginEvent.OnPasswordChange -> {
                onPasswordChange(event.password)
            }

            LoginEvent.OnLoginClick -> onLoginClick()

            LoginEvent.OnPasswordVisibilityToggle -> {
                _loginState.update { currentState ->
                    currentState.copy(showPassword = !currentState.showPassword)
                }
            }

            is LoginEvent.UpdateGeoLocation -> {
                _loginState.update { currentState ->
                    currentState.copy(geoLocation = event.geoLocation)
                }
            }

            is LoginEvent.ContinueLoginWithFlaggedLocation -> {
                Log.d("LoginViewModel", "Continuing with flagged location")
                _showDangerousLocationModal.value = false to null
                viewModelScope.launch {
                    _loginResultEvent.emit(LoginResultEvent.LoginSuccessful(event.token))
                }
            }

            is LoginEvent.VerifyIdentity -> {
                Log.d("LoginViewModel", "Verifying identity")
                _showDangerousLocationModal.value = false to null
                if (android.provider.Settings.canDrawOverlays(event.context)) {
                    Log.d("LoginViewModel", "Permission already granted, emitting NavigateToVerification")
                    viewModelScope.launch {
                        _loginResultEvent.emit(LoginResultEvent.NavigateToVerification(event.token))
                    }
                } else {
                    _showDisplayOverAppModal.value = true to event.token
                }
            }

            LoginEvent.DismissDangerousLocationModal -> {
                Log.d("LoginViewModel", "Dismissing dangerous location modal")
                _showDangerousLocationModal.value = false to null
                viewModelScope.launch {
                    _loginResultEvent.emit(LoginResultEvent.NavigateToLanding)
                }
            }

            LoginEvent.ShowDisplayOverAppModal -> {
                Log.d("LoginViewModel", "Showing display over app modal")
                _showDisplayOverAppModal.value = true to null
            }

            LoginEvent.DismissDisplayOverAppModal -> {
                Log.d("LoginViewModel", "Dismissing display over app modal")
                _showDisplayOverAppModal.value = false to null
            }

            LoginEvent.OpenDisplayOverAppSettings -> {
                Log.d("LoginViewModel", "Opening display over app settings")
                _showDisplayOverAppModal.value = false to null
                viewModelScope.launch {
                    _loginResultEvent.emit(LoginResultEvent.OpenDisplayOverAppSettings)
                }
            }

            is LoginEvent.StoreTokenForVerification -> {
                Log.d("LoginViewModel", "Storing token for verification: ${event.token}")
                _loginState.update { currentState ->
                    currentState.copy(token = event.token)
                }
            }

            LoginEvent.ClearStoredToken -> {
                Log.d("LoginViewModel", "Clearing stored token")
                _loginState.update { currentState ->
                    currentState.copy(token = null)
                }
            }

            is LoginEvent.EmitNavigateToVerification -> {
                Log.d("LoginViewModel", "Emitting NavigateToVerification event with token: ${event.token}")
                viewModelScope.launch {
                    _loginResultEvent.emit(LoginResultEvent.NavigateToVerification(event.token))
                    // Clear the stored token after emitting the event
                    _loginState.update { currentState ->
                        currentState.copy(token = null)
                    }
                }
            }
        }
    }

    private suspend fun checkLocationSafety(token: String, locationCheck: LocationCheck): Boolean {
        return try {
            Log.d("LoginViewModel", "Starting location safety check")
            val locationResponse = moneyGuardUtility?.checkLocation(token, locationCheck)
            Log.d("LoginViewModel", "Got location response: ${locationResponse?.data}")
            // If response.data is empty, location is safe
            val isSafe = locationResponse?.data?.isEmpty() ?: true
            Log.d("LoginViewModel", "Location is ${if (isSafe) "safe" else "unsafe"}")
            isSafe
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error checking location safety", e)
            false
        }
    }

    private fun onLoginClick() {
        viewModelScope.launch {
            val email = loginState.value.email
            val password = loginState.value.password

            if (email.isEmpty() || password.isEmpty())
                return@launch

            loginRepository.login(email, password)
                .onStart {
                    _loginState.update { state ->
                        state.copy(isLoading = true, errorMessage = null)
                    }
                }
                .catch {
                    _loginState.update { state ->
                        state.copy(isLoading = false, errorMessage = it.message)
                    }
                }
                .collect { sessionId ->
                    getMoneyGuardSession(sessionId)
                }
        }
    }

    private fun getMoneyGuardSession(partnerBankSessionId: String) {
        viewModelScope.launch {
            if (moneyGuardAuthentication == null) {
                _loginState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = "MoneyGuard authentication service is not available"
                    )
                }
                return@launch
            }

            try {
                moneyGuardAuthentication?.register(WIMIKA_BANK, partnerBankSessionId)
                    ?.catch { error ->
                        Log.e("LoginViewModel", "Error in registration flow", error)
                        _loginState.update { state ->
                            state.copy(isLoading = false, errorMessage = error.message)
                        }
                    }
                    ?.collect { result ->
                        when (result) {
                            is MoneyGuardResult.Failure -> {
                                Log.e(
                                    "LoginViewModel",
                                    "Registration failed: ${result.error.message}"
                                )
                                _loginState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        errorMessage = result.error.message
                                    )
                                }
                            }

                            MoneyGuardResult.Loading -> {
                                Log.d("LoginViewModel", "Registration in progress")
                            }

                            is MoneyGuardResult.Success<SessionResponse> -> {
                                Log.d("LoginViewModel", "Registration successful")
                                val session = result.data as? SessionResponse

                                _loginState.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        errorMessage = null,
                                        sessionId = session?.token
                                    )
                                }

                                if (session != null) {
                                    try {
                                        // Save user's first name
                                        session.userDetails?.firstName?.let { firstName ->
                                            preferenceManager?.saveUserFirstName(firstName)
                                        }

                                        // Check MoneyGuard status before performing credential check
                                        val moneyGuardStatus =
                                            moneyGuardUtility?.checkMoneyguardStatus(session.token)
                                        Log.d(
                                            "LoginViewModel",
                                            "MoneyGuard Status: $moneyGuardStatus"
                                        )

                                        if (moneyGuardStatus == MoneyGuardAppStatus.Active ||
                                            moneyGuardStatus == MoneyGuardAppStatus.ValidPolicyAppNotInstalled
                                        ) {
                                            try {
                                                // Only perform credential check if status is Active or ValidPolicyAppNotInstalled
                                                val scanResult = performCredentialChecks(
                                                    session.token,
                                                    loginState.value.email,
                                                    loginState.value.password
                                                )
                                                Log.d(
                                                    "LoginViewModel",
                                                    "Credential Check Result: ${scanResult.status}"
                                                )
                                                _loginResultEvent.emit(
                                                    LoginResultEvent.CredentialCheckSuccessful(
                                                        scanResult.status
                                                    )
                                                )
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "LoginViewModel",
                                                    "Error during credential check",
                                                    e
                                                )
                                            }
                                        } else {
                                            Log.d(
                                                "LoginViewModel",
                                                "Skipping credential check due to MoneyGuard status: $moneyGuardStatus"
                                            )
                                        }

                                        // Check location safety
                                        val currentLocation = loginState.value.geoLocation
                                        if (currentLocation == null) {
                                            _loginResultEvent.emit(LoginResultEvent.LoginFailed(error = "Cannot get your current location"))
                                            return@collect
                                        }

                                        val locationCheck = LocationCheck(
                                            latitude = currentLocation.lat,
                                            longitude = currentLocation.lon
                                        )

                                        val isLocationSafe = checkLocationSafety(session.token, locationCheck)
                                        if (isLocationSafe) {
                                            Log.d("LoginViewModel", "Location is safe, proceeding to dashboard")
                                            _loginState.update { state ->
                                                state.copy(sessionId = session.token)
                                            }
                                            _loginResultEvent.emit(LoginResultEvent.LoginSuccessful(session.token))
                                        } else {
                                            Log.d("LoginViewModel", "Location is unsafe, showing modal")
                                            _showDangerousLocationModal.value = true to session.token
                                        }

                                    } catch (e: Exception) {
                                        Log.e(
                                            "LoginViewModel",
                                            "Error in MoneyGuard session handling",
                                            e
                                        )
                                        _loginState.update { state ->
                                            state.copy(isLoading = false, errorMessage = e.message)
                                        }
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error in getMoneyGuardSession", e)
                _loginState.update { state ->
                    state.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }

    private fun onEmailChange(email: String) {
        _loginState.update { currentState ->
            currentState.copy(email = email)
        }
    }

    private fun onPasswordChange(password: String) {
        _loginState.update { currentState ->
            currentState.copy(password = password)
        }
    }

    private suspend fun performCredentialChecks(
        sessionToken: String,
        username: String,
        password: String
    ) = suspendCoroutine<CredentialScanResult> { coroutine ->
        val credential = Credential(
            username = username,
            passwordStartingCharactersHash = password.substring(password.length - 3)
                .computeSha256Hash(),
            domain = "wimika.ng",
            hashAlgorithm = HashAlgorithm.SHA256,
        )

        moneyGuardAuthentication?.credentialCheck(
            sessionToken, credential,
            onResult = { result ->
                when (result) {
                    is MoneyGuardResult.Failure -> {
                        coroutine.resumeWithException(result.error)
                    }

                    MoneyGuardResult.Loading -> {

                    }

                    is MoneyGuardResult.Success<CredentialScanResult> -> {
                        coroutine.resume(result.data)
                    }
                }
            }
        ) ?: run {
            coroutine.resumeWithException(IllegalStateException("MoneyGuard authentication service is not available"))
        }
    }
}