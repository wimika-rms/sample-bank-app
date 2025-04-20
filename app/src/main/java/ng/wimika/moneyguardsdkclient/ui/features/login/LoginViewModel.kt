package ng.wimika.moneyguardsdkclient.ui.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

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
        when(event) {
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
        _loginState.update { currentState ->
            currentState.copy(isLoading = true)
        }

        viewModelScope.launch {
            delay(3000L)
            _loginState.update { currentState ->
                currentState.copy(isLoading = false)
            }
        }
    }


}