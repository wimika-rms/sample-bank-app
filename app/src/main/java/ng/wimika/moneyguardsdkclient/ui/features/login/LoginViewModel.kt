package ng.wimika.moneyguardsdkclient.ui.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguard_sdk.services.authentication.MoneyGuardAuthentication
import ng.wimika.moneyguard_sdk_auth.datasource.auth_service.models.SessionResponse
import ng.wimika.moneyguard_sdk_commons.types.MoneyGuardResult
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepository
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepositoryImpl

class LoginViewModel : ViewModel() {

    private val loginRepository: LoginRepository = LoginRepositoryImpl()

    private val moneyGuardAuthentication: MoneyGuardAuthentication? by lazy {
        MoneyGuardClientApp.sdkService?.authentication()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()


    companion object {
        private const val WIMIKA_BANK = 101
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
                return@launch
            }

            moneyGuardAuthentication?.register(WIMIKA_BANK, partnerBankSessionId)
                ?.catch { error ->
                    _loginState.update { state ->
                        state.copy(isLoading = false, errorMessage = error.message)
                    }
                }
                ?.collect { result ->
                    when (result) {
                        is MoneyGuardResult.Failure -> {
                            _loginState.update { state ->
                                state.copy(isLoading = false, errorMessage = result.error.message)
                            }
                        }

                        MoneyGuardResult.Loading -> {}

                        is MoneyGuardResult.Success<SessionResponse> -> {
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
                            }

                        }
                    }
                }
        }
    }

}