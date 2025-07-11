package ng.wimika.moneyguardsdkclient.ui.features.login

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.setValue
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation
import ng.wimika.moneyguardsdkclient.utils.PermissionUtils
import android.content.Context
import android.location.LocationManager
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.*
import ng.wimika.moneyguardsdkclient.R
import ng.wimika.moneyguardsdkclient.utils.LocationViewModel
import ng.wimika.moneyguardsdkclient.utils.LocationViewModelFactory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.navigation.NavController
import ng.wimika.moneyguardsdkclient.ui.navigation.Screen
import android.os.Bundle
import ng.wimika.moneyguardsdkclient.ui.features.landing.Landing
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import android.util.Log
import ng.wimika.moneyguardsdkclient.ui.navigation.Routes
import android.content.Intent
import android.provider.Settings

@Serializable
object Login

var tokenStr = "";

@Composable
fun LoginScreen(
    loginState: LoginState,
    onEvent: (LoginEvent) -> Unit,
    showDangerousLocationModal: Pair<Boolean, String?>,
    showDisplayOverAppModal: Pair<Boolean, String?>
) {
    Log.d("LoginScreen", "Rendering with state: $loginState, modal state: $showDangerousLocationModal")

    val context = LocalContext.current
    
    // Show dangerous location modal if needed
    if (showDangerousLocationModal.first) {
        Log.d("LoginScreen", "Showing dangerous location modal")
        AlertDialog(
            onDismissRequest = {
                Log.d("LoginScreen", "Modal dismissed")
                onEvent(LoginEvent.DismissDangerousLocationModal)
            },
            title = { Text("Warning: Suspicious Location") },
            text = {
                Text("We've detected that you're logging in from a location that has been flagged as suspicious. This could be due to:\n\n" +
                        "• Unusual login location\n" +
                        "• High-risk area\n" +
                        "• Previous security incidents\n\n" +
                        "Please verify your identity to continue.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d("LoginScreen", "Verify button clicked")
                        showDangerousLocationModal.second?.let {
                            onEvent(LoginEvent.VerifyIdentity(it, context))
                        }
                    }
                ) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("LoginScreen", "Cancel button clicked")
                        onEvent(LoginEvent.DismissDangerousLocationModal)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Show display over app modal if needed
    if (showDisplayOverAppModal.first) {
        Log.d("LoginScreen", "Showing display over app modal")
        AlertDialog(
            onDismissRequest = {
                Log.d("LoginScreen", "Display over app modal dismissed")
                onEvent(LoginEvent.DismissDisplayOverAppModal)
            },
            title = { Text("Permission Required") },
            text = {
                Text("To perform verification, we need permission to display over other apps. This allows us to:\n\n" +
                        "• Monitor typing patterns securely\n" +
                        "• Ensure verification accuracy\n" +
                        "• Protect your account\n\n" +
                        "Search for \"MoneyGuard Sample Bank App\" in your device settings and enable the 'Display over other apps' permission.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        Log.d("LoginScreen", "Proceed to settings clicked")
                        onEvent(LoginEvent.OpenDisplayOverAppSettings)
                    }
                ) {
                    Text("Proceed to Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        Log.d("LoginScreen", "Dismiss display over app modal clicked")
                        onEvent(LoginEvent.DismissDisplayOverAppModal)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

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
                                painter = painterResource(if (loginState.showPassword)
                                    R.drawable.ic_eye_close
                                else
                                    R.drawable.ic_eye
                                ),
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

                if (loginState.errorMessage != null) {
                    Text(
                        text = loginState.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Version number in bottom right
            Text(
                text = "v0.2.4",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun LoginDestination(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(
            context = LocalContext.current,
            locationManager = LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        )
    ),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val modalState by viewModel.showDangerousLocationModal.collectAsStateWithLifecycle()
    val displayOverAppModalState by viewModel.showDisplayOverAppModal.collectAsStateWithLifecycle()
    val locationState by locationViewModel.locationState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasLocationPermissions by remember {
        mutableStateOf(
            PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    && PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermissions = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(hasLocationPermissions) {
        if (!hasLocationPermissions) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return@LaunchedEffect
        }

        locationViewModel.getCurrentLocation()
    }

    LaunchedEffect(locationState) {
        locationState?.let { location ->
            viewModel.onEvent(LoginEvent.UpdateGeoLocation(location))
        }
    }

    // Check for display over app permission when returning from settings
    LaunchedEffect(Unit) {
        // Check if we have a stored token and permission is granted
        val storedToken = state.token
        if (storedToken != null && Settings.canDrawOverlays(context)) {
            Log.d("LoginDestination", "Permission granted, emitting NavigateToVerification event with token: $storedToken")
            viewModel.onEvent(LoginEvent.EmitNavigateToVerification(storedToken))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loginResultEvent.collect { event ->
            Log.d("LoginDestination", "Received login result event: $event")
            when(event) {
                is LoginResultEvent.CredentialCheckSuccessful -> {
                    Toast.makeText(
                        context,
                        "Credential check: ${event.result.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is LoginResultEvent.LoginSuccessful -> {
                    Log.d("LoginDestination", "Login successful, navigating to dashboard with token: ${event.token}")
                    navController.navigate("dashboard/${event.token}") {
                        popUpTo(0) { inclusive = true }
                    }
                }

                is LoginResultEvent.LoginFailed -> {
                    Toast.makeText(
                        context,
                        event.error,
                        Toast.LENGTH_LONG
                    ).show()
                }

                is LoginResultEvent.NavigateToVerification -> {
                    Log.d("LoginDestination", "Navigating to verification")
                    navController.navigate("verify_typing_profile/${event.token}")
                }

                LoginResultEvent.OpenDisplayOverAppSettings -> {
                    Log.d("LoginDestination", "Opening display over app settings")
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        context.startActivity(intent)
                        // Store the token for when user returns from settings
                        viewModel.onEvent(LoginEvent.StoreTokenForVerification(displayOverAppModalState.second ?: ""))
                    } catch (e: Exception) {
                        Log.e("LoginDestination", "Error opening display over app settings", e)
                        Toast.makeText(context, "Could not open settings", Toast.LENGTH_SHORT).show()
                    }
                }

                LoginResultEvent.NavigateToLanding -> {
                    Log.d("TokenDebug-LoginDestination", "Navigating to landing with token: ${state.sessionId}")
                    val navOptions = navOptions {
                        popUpTo(0) { inclusive = true }
                    }
                    navController.navigate("landing/${state.sessionId}", navOptions)
                }
            }
        }
    }

    LoginScreen(
        loginState = state,
        onEvent = viewModel::onEvent,
        showDangerousLocationModal = modalState,
        showDisplayOverAppModal = displayOverAppModalState
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            loginState = LoginState(),
            onEvent = { },
            showDangerousLocationModal = false to null,
            showDisplayOverAppModal = false to null
        )
    }
}