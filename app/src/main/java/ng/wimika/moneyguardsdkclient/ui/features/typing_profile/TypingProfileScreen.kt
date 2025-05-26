package ng.wimika.moneyguardsdkclient.ui.features.typing_profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ng.wimika.moneyguard_sdk.MoneyGuardSdk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingProfileScreen(
    viewModel: TypingProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Start the typing profile service when screen loads
    LaunchedEffect(Unit) {
        val sdk = MoneyGuardSdk.initialize(context)
        val typingProfile = sdk.getTypingProfile()
        typingProfile.startService(context as android.app.Activity, intArrayOf(android.R.id.text1))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Typing Profile Enrollment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Enter text to match your typing pattern",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Enter text to match") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )

                        Button(
                            onClick = {
                                viewModel.matchTypingProfile(text, context)
                                showResultDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = text.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Match Typing Profile")
                            }
                        }
                    }
                }
            }

            // Error Snackbar
            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }

    // Result Dialog
    if (showResultDialog && result != null) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("Typing Profile Result") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultItem("Success", result?.success.toString())
                    ResultItem("Matched", result?.matched.toString())
                    ResultItem("High Confidence", result?.highConfidence.toString())
                    ResultItem("Enrolled on this device", result?.isEnrolledOnThisDevice.toString())
                    ResultItem("Has other enrollments", result?.hasOtherEnrollments.toString())
                    result?.message?.let { ResultItem("Message", it) }
                }
            },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ResultItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
} 