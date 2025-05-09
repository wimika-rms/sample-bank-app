package ng.wimika.moneyguardsdkclient.ui.features.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock


@Serializable
object Login

sealed class LoginEvent {
    data object OnLoginClick : LoginEvent()
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data object OnPasswordVisibilityToggle : LoginEvent()
}


@Composable
fun LoginDestination(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loginResultEvent.collect { event ->
            when(event) {
                is LoginResultEvent.CredentialCheckSuccessful -> {
                    Toast.makeText(context, "Credential check: " + event.result.name, Toast.LENGTH_LONG).show()
                }

                is LoginResultEvent.LoginSuccessful -> {
                    onLoginSuccess()
                }
            }
        }
    }

    LoginScreen(
        loginState = state,
        onEvent = viewModel::onEvent
    )
}



@Composable
fun LoginScreen(
    loginState: LoginState,
    onEvent: (LoginEvent) -> Unit
) {
    Surface(
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Login", style = MaterialTheme.typography.displaySmall)

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = loginState.email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {
                        onEvent(LoginEvent.OnEmailChange(it))
                    },
                    label = { Text("Email") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (loginState.showPassword)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    value = loginState.password,
                    onValueChange = {
                        onEvent(LoginEvent.OnPasswordChange(it))
                    },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = { onEvent(LoginEvent.OnPasswordVisibilityToggle) }) {
                            Icon(
                                imageVector = if (loginState.showPassword) 
                                    Icons.Default.Lock
                                else 
                                    Icons.Default.Edit,
                                contentDescription = if (loginState.showPassword) 
                                    "Hide password" 
                                else 
                                    "Show password"
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = {
                        onEvent(LoginEvent.OnLoginClick)
                    }
                ) {
                    if (loginState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }

                    if (!loginState.isLoading) {
                        Text("Login")
                    }
                }

                if (loginState.sessionId != null) {
                    Text("Logged in: Successful",
                        color = Color.DarkGray
                    )
                }

                if (loginState.errorMessage != null) {
                    Text(
                        text = loginState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            loginState = LoginState(),
            onEvent = {

            }
        )
    }
}