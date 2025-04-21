package ng.wimika.moneyguardsdkclient.ui.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepository
import ng.wimika.moneyguardsdkclient.ui.features.login.data.LoginRepositoryImpl

class LoginViewModel : ViewModel() {

    private val loginRepository: LoginRepository = LoginRepositoryImpl()

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()


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
                        state.copy(isLoading = true)
                    }
                }
                .catch {
                    _loginState.update { state ->
                        state.copy(isLoading = false, errorMessage = it.message)
                    }
                }
                .collect { sessionId ->
                    _loginState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = null,
                            sessionId = sessionId
                        )
                    }
                }
        }
    }


}