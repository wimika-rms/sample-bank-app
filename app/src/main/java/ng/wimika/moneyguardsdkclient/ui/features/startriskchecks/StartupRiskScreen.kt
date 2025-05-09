package ng.wimika.moneyguardsdkclient.ui.features.startriskchecks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk_commons.types.SpecificRisk
import androidx.compose.ui.tooling.preview.Preview


@Serializable
object StartupRiskScreen

private fun displayWarningMessages(issues: List<SpecificRisk>): List<RiskMessage> {
    val messages = mutableListOf<RiskMessage>()

    issues.forEach { issue ->
        when (issue.name) {
            RiskConstants.SPECIFIC_RISK_DEVICE_SECURITY_MISCONFIGURATION_USB_DEBUGGING_NAME -> {
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "USB debugging is enabled on your device. Your login credentials may be compromised.",
                        primaryButtonText = "Disable",
                        secondaryButtonText = "Proceed to launch anyway"
                    )
                )
            }

            RiskConstants.SPECIFIC_RISK_NETWORK_WIFI_ENCRYPTION_NAME,
            RiskConstants.SPECIFIC_RISK_NETWORK_WIFI_PASSWORD_PROTECTION_NAME -> {
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Unsecured WIFI detected. Your digital banking activities may be compromised.",
                        primaryButtonText = "Disconnect",
                        secondaryButtonText = "Proceed"
                    )
                )
            }
        }
    }
    return messages
}

private fun displayDoNotLaunchMessages(issues: List<SpecificRisk>): List<RiskMessage> {
    val messages = mutableListOf<RiskMessage>()

    issues.forEach { issue ->
        when (issue.name) {
            RiskConstants.SPECIFIC_RISK_DEVICE_ROOT_OR_JAILBREAK_NAME -> {
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Device security is compromised. We strongly advise you not to log into your bank app.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Continue to login"
                    )
                )
            }

            RiskConstants.SPECIFIC_RISK_NETWORK_DNS_SPOOFING_NAME -> {
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Spoofing Detected. Your banking activities are at risk if you continue.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Proceed to login"
                    )
                )
            }

            RiskConstants.SPECIFIC_RISK_DEVICE_SECURITY_MISCONFIGURATION_LOW_QUALITY_DEVICE_PASSWORD_NAME -> {
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Your device doesn't have a password set. We recommend setting one for better security before logging into your bank app.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Continue to login"
                    )
                )
            }
        }
    }
    return messages
}

data class RiskMessage(
    val title: String,
    val message: String,
    val primaryButtonText: String,
    val secondaryButtonText: String
)

@Composable
fun RiskModal(
    isSevere: Boolean,
    issues: List<SpecificRisk>,
    onDismiss: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    val messages =
        if (isSevere) displayDoNotLaunchMessages(issues) else displayWarningMessages(issues)

    if (messages.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isSevere) Color.Red else Color(0xFFFFA000)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = messages.first().title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    messages.forEach { message ->
                        Text(
                            text = message.message,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onSecondaryAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSevere) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                ) {
                    Text(messages.first().secondaryButtonText)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss
                ) {
                    Text(messages.first().primaryButtonText)
                }
            }
        )
    }
}

@Composable
fun StartupRiskDestination(
    viewModel: StartupRiskViewModel = viewModel(),
    launchLoginScreen: () -> Unit
) {
    val state by viewModel.startupRiskState.collectAsStateWithLifecycle()

    if (state.currentRiskEvent != null) {
        when (val event = state.currentRiskEvent) {
            is StartupRiskResultEvent.SevereRisk -> {
                RiskModal(
                    isSevere = true,
                    issues = event.issues,
                    onDismiss = {
                        viewModel.dismissRiskModal()
                        // Exit the app or handle severe risk case
                    },
                    onSecondaryAction = {
                        viewModel.dismissRiskModal()
                        launchLoginScreen()
                    }
                )
            }

            is StartupRiskResultEvent.WarningRisk -> {
                RiskModal(
                    isSevere = false,
                    issues = event.issues,
                    onDismiss = {
                        viewModel.dismissRiskModal()
                        // Handle warning risk case
                    },
                    onSecondaryAction = {
                        viewModel.dismissRiskModal()
                        launchLoginScreen()
                    }
                )
            }

            StartupRiskResultEvent.RiskFree -> {
                launchLoginScreen()
            }
            else -> {}
        }
    }

    StartupRiskScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun StartupRiskScreen(
    state: StartupRiskState,
    onEvent: (StartupRiskEvent) -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()

                Text(
                    text = "Loading Startup Risks"
                )
            }

            if (!state.isLoading) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onEvent(StartupRiskEvent.StartStartUpRiskCheck)
                    },
                    enabled = state.shouldEnableButton,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Proceed to login")
                }
            }

        }
    }
}

@Preview(name = "Risk Free", showBackground = true)
@Composable
private fun StartupRiskScreenRiskFreePreview() {
    MaterialTheme {
        StartupRiskScreen(
            state = StartupRiskState(
                isLoading = false,
                isRiskFree = true,
                isWarningRisk = false
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Warning Risk", showBackground = true)
@Composable
private fun StartupRiskScreenWarningPreview() {
    MaterialTheme {
        StartupRiskScreen(
            state = StartupRiskState(
                isLoading = false,
                isRiskFree = false,
                isWarningRisk = true
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Severe Risk", showBackground = true)
@Composable
private fun StartupRiskScreenSeverePreview() {
    MaterialTheme {
        StartupRiskScreen(
            state = StartupRiskState(
                isLoading = false,
                isRiskFree = false,
                isWarningRisk = false
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun StartupRiskScreenLoadingPreview() {
    MaterialTheme {
        StartupRiskScreen(
            state = StartupRiskState(
                isLoading = true,
                isRiskFree = false,
                isWarningRisk = false
            ),
            onEvent = {}
        )
    }
}