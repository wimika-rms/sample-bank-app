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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk_commons.types.SpecificRisk
import ng.wimika.moneyguard_sdk_commons.types.RiskStatus
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.distinctUntilChanged


@Serializable
object StartupRiskScreen

private fun displayWarningMessages(issues: List<SpecificRisk>): List<RiskMessage> {
    android.util.Log.d("StartupRiskScreen", "displayWarningMessages called with ${issues.size} issues")
    val messages = mutableListOf<RiskMessage>()

    issues.forEach { issue ->
        android.util.Log.d("StartupRiskScreen", "Processing issue: ${issue.name}")
        when (issue.name) {
            "Security Misconfiguration USB Debugging" -> {
                android.util.Log.d("StartupRiskScreen", "Matched USB Debugging risk")
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "USB debugging is enabled on your device. Your login credentials may be compromised.",
                        primaryButtonText = "Disable",
                        secondaryButtonText = "Proceed to launch anyway"
                    )
                )
            }

            "WiFi Encryption",
            "WiFi Password Protection" -> {
                android.util.Log.d("StartupRiskScreen", "Matched WiFi risk")
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Unsecured WIFI detected. Your digital banking activities may be compromised.",
                        primaryButtonText = "Disconnect",
                        secondaryButtonText = "Proceed"
                    )
                )
            }
            else -> {
                android.util.Log.d("StartupRiskScreen", "No match found for risk: ${issue.name}")
            }
        }
    }
    android.util.Log.d("StartupRiskScreen", "Generated ${messages.size} warning messages")
    return messages
}

private fun displayDoNotLaunchMessages(issues: List<SpecificRisk>): List<RiskMessage> {
    android.util.Log.d("StartupRiskScreen", "displayDoNotLaunchMessages called with ${issues.size} issues")
    val messages = mutableListOf<RiskMessage>()

    issues.forEach { issue ->
        android.util.Log.d("StartupRiskScreen", "Processing issue: ${issue.name}")
        when (issue.name) {
            "Root/Jail Break" -> {
                android.util.Log.d("StartupRiskScreen", "Matched Root/Jailbreak risk")
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Device security is compromised. We strongly advise you not to log into your bank app.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Continue to login"
                    )
                )
            }

            "DNS Spoofing" -> {
                android.util.Log.d("StartupRiskScreen", "Matched DNS Spoofing risk")
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Spoofing Detected. Your banking activities are at risk if you continue.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Proceed to login"
                    )
                )
            }

            "Security Misconfiguration Low Quality Device Password" -> {
                android.util.Log.d("StartupRiskScreen", "Matched Low Quality Password risk")
                messages.add(
                    RiskMessage(
                        title = "Launch Warning",
                        message = "Your device doesn't have a password set. We recommend setting one for better security before logging into your bank app.",
                        primaryButtonText = "Ok",
                        secondaryButtonText = "Continue to login"
                    )
                )
            }
            else -> {
                android.util.Log.d("StartupRiskScreen", "No match found for risk: ${issue.name}")
            }
        }
    }
    android.util.Log.d("StartupRiskScreen", "Generated ${messages.size} do not launch messages")
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
    android.util.Log.d("StartupRiskScreen", "RiskModal composable called - isSevere: $isSevere, issues: ${issues.size}")
    val messages =
        if (isSevere) displayDoNotLaunchMessages(issues) else displayWarningMessages(issues)
    
    android.util.Log.d("StartupRiskScreen", "Generated messages: ${messages.size}")

    if (messages.isNotEmpty()) {
        android.util.Log.d("StartupRiskScreen", "Creating AlertDialog with first message: ${messages.first()}")
        AlertDialog(
            onDismissRequest = {
                android.util.Log.d("StartupRiskScreen", "AlertDialog onDismissRequest called")
                onDismiss()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = {
                        android.util.Log.d("StartupRiskScreen", "AlertDialog confirm button clicked")
                        onSecondaryAction()
                    },
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
                    onClick = {
                        android.util.Log.d("StartupRiskScreen", "AlertDialog dismiss button clicked")
                        onDismiss()
                    }
                ) {
                    Text(messages.first().primaryButtonText)
                }
            }
        )
    } else {
        android.util.Log.w("StartupRiskScreen", "No messages generated for modal")
    }
}

@Composable
fun StartupRiskDestination(
    viewModel: StartupRiskViewModel = viewModel(),
    launchLoginScreen: () -> Unit
) {
    val state by viewModel.startupRiskState.collectAsStateWithLifecycle()
    var showModal by remember { mutableStateOf(false) }
    var modalData by remember { mutableStateOf<StartupRiskResultEvent?>(null) }
    var shouldNavigateToLogin by remember { mutableStateOf(false) }
    var hasAcknowledgedRisks by remember { mutableStateOf(false) }

    // Run risk checks immediately when screen is launched
    LaunchedEffect(Unit) {
        android.util.Log.d("StartupRiskScreen", "Starting initial risk check")
        viewModel.onEvent(StartupRiskEvent.StartStartUpRiskCheck)
    }

    // Handle navigation after modal dismissal
    LaunchedEffect(shouldNavigateToLogin) {
        if (shouldNavigateToLogin) {
            android.util.Log.d("StartupRiskScreen", "Navigating to login screen")
            launchLoginScreen()
            shouldNavigateToLogin = false
        }
    }

    // Handle risk check results
    LaunchedEffect(Unit) {
        viewModel.uiEvent.distinctUntilChanged().collect { event ->
            android.util.Log.d("StartupRiskScreen", "Received UI event: $event")
            when(event) {
                StartupRiskResultEvent.RiskFree -> {
                    android.util.Log.d("StartupRiskScreen", "No risks detected")
                    hasAcknowledgedRisks = true
                }
                is StartupRiskResultEvent.SevereRisk -> {
                    android.util.Log.d("StartupRiskScreen", "Severe risks detected: ${event.issues.size}")
                    event.issues.forEach { risk ->
                        android.util.Log.d("StartupRiskScreen", "Severe risk: ${risk.name}, status: ${risk.status}")
                    }
                    modalData = event
                    showModal = true
                    hasAcknowledgedRisks = false
                    android.util.Log.d("StartupRiskScreen", "Modal state updated - showModal: $showModal, modalData: $modalData")
                }
                is StartupRiskResultEvent.WarningRisk -> {
                    android.util.Log.d("StartupRiskScreen", "Warning risks detected: ${event.issues.size}")
                    event.issues.forEach { risk ->
                        android.util.Log.d("StartupRiskScreen", "Warning risk: ${risk.name}, status: ${risk.status}")
                    }
                    modalData = event
                    showModal = true
                    hasAcknowledgedRisks = false
                    android.util.Log.d("StartupRiskScreen", "Modal state updated - showModal: $showModal, modalData: $modalData")
                }
            }
        }
    }

    // Show modal for each risk
    if (showModal && modalData != null) {
        android.util.Log.d("StartupRiskScreen", "Attempting to show modal - showModal: $showModal, modalData: $modalData")
        when (val event = modalData) {
            is StartupRiskResultEvent.SevereRisk -> {
                android.util.Log.d("StartupRiskScreen", "Showing severe risk modal")
                RiskModal(
                    isSevere = true,
                    issues = event.issues,
                    onDismiss = {
                        android.util.Log.d("StartupRiskScreen", "Severe risk modal dismissed")
                        showModal = false
                        modalData = null
                        hasAcknowledgedRisks = true
                    },
                    onSecondaryAction = {
                        android.util.Log.d("StartupRiskScreen", "Severe risk modal secondary action")
                        showModal = false
                        modalData = null
                        shouldNavigateToLogin = true
                    }
                )
            }
            is StartupRiskResultEvent.WarningRisk -> {
                android.util.Log.d("StartupRiskScreen", "Showing warning risk modal")
                RiskModal(
                    isSevere = false,
                    issues = event.issues,
                    onDismiss = {
                        android.util.Log.d("StartupRiskScreen", "Warning risk modal dismissed")
                        showModal = false
                        modalData = null
                        hasAcknowledgedRisks = true
                    },
                    onSecondaryAction = {
                        android.util.Log.d("StartupRiskScreen", "Warning risk modal secondary action")
                        showModal = false
                        modalData = null
                        shouldNavigateToLogin = true
                    }
                )
            }
            else -> {
                android.util.Log.d("StartupRiskScreen", "Unknown modal data type")
            }
        }
    }

    StartupRiskScreen(
        state = state,
        hasAcknowledgedRisks = hasAcknowledgedRisks,
        onEvent = { event ->
            android.util.Log.d("StartupRiskScreen", "Received event: $event")
            when (event) {
                StartupRiskEvent.StartStartUpRiskCheck -> {
                    android.util.Log.d("StartupRiskScreen", "Manual risk check triggered")
                }
                StartupRiskEvent.ProceedToLogin -> {
                    android.util.Log.d("StartupRiskScreen", "Proceed to login clicked")
                    shouldNavigateToLogin = true
                }
            }
        },
    )
}


@Composable
fun StartupRiskScreen(
    state: StartupRiskState,
    hasAcknowledgedRisks: Boolean,
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
                    text = "Checking for security risks..."
                )
            } else {
                // Show risk status message
                val activeRisks = state.risks.filter { it.status != RiskStatus.RISK_STATUS_SAFE }
                if (activeRisks.isEmpty() || hasAcknowledgedRisks) {
                    Text(
                        text = if (activeRisks.isEmpty()) "No startup risks observed" else "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Show proceed button when there are no risks or risks have been acknowledged
                     Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(StartupRiskEvent.ProceedToLogin) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Proceed to login")
                    }
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
            ),
            hasAcknowledgedRisks = false,
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
            ),
            hasAcknowledgedRisks = false,
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
            ),
            hasAcknowledgedRisks = false,
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
            ),
            hasAcknowledgedRisks = false,
            onEvent = {}
        )
    }
}