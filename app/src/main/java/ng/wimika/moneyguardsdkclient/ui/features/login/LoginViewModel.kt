package ng.wimika.moneyguardsdkclient.ui.features.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private var onLoginSuccess: (() -> Unit)? = null

    companion object {
        private const val WIMIKA_BANK = 101
    }


    fun logOut() {
        viewModelScope.launch {
            moneyGuardAuthentication?.logout()
            preferenceManager?.clear()
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
                    state.copy(isLoading = false, errorMessage = "MoneyGuard authentication service is not available")
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
                                Log.e("LoginViewModel", "Registration failed: ${result.error.message}")
                                _loginState.update { state ->
                                    state.copy(isLoading = false, errorMessage = result.error.message)
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
                                    preferenceManager?.saveMoneyGuardToken(session.token)

                                    try {
                                        // Check MoneyGuard status before performing credential check
                                        val moneyGuardStatus = moneyGuardUtility?.checkMoneyguardStatus(session.token)
                                        Log.d("LoginViewModel", "MoneyGuard Status: $moneyGuardStatus")
                                        
                                        if (moneyGuardStatus == MoneyGuardAppStatus.Active || 
                                            moneyGuardStatus == MoneyGuardAppStatus.ValidPolicyAppNotInstalled) {
                                            try {
                                                // Only perform credential check if status is Active or ValidPolicyAppNotInstalled
                                                val scanResult = performCredentialChecks(
                                                    session.token,
                                                    loginState.value.email,
                                                    loginState.value.password
                                                )
                                                Log.d("LoginViewModel", "Credential Check Result: ${scanResult.status}")
                                                _loginResultEvent.emit(LoginResultEvent.CredentialCheckSuccessful(scanResult.status))
                                            } catch (e: Exception) {
                                                Log.e("LoginViewModel", "Error during credential check", e)
                                                // Don't update error state for credential check failures
                                            }
                                        } else {
                                            Log.d("LoginViewModel", "Skipping credential check due to MoneyGuard status: $moneyGuardStatus")
                                        }

                                        // Emit login success after credential check
                                        _loginResultEvent.emit(LoginResultEvent.LoginSuccessful(session.token))
                                    } catch (e: Exception) {
                                        Log.e("LoginViewModel", "Error in MoneyGuard session handling", e)
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


    private suspend fun performCredentialChecks(
        sessionToken: String,
        username: String,
        password: String
    ) = suspendCoroutine<CredentialScanResult> { coroutine ->
        val credential = Credential(
            username = username,
            passwordStartingCharactersHash = password.substring(password.length -3).computeSha256Hash(),
            domain = "wimika.ng",
            hashAlgorithm = HashAlgorithm.SHA256,
        )

        moneyGuardAuthentication?.credentialCheck(sessionToken, credential,
            onResult = { result ->
                when(result) {
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