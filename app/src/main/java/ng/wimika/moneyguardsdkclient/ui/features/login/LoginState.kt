package ng.wimika.moneyguardsdkclient.ui.features.login

data class LoginState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val sessionId: String? = null
)
