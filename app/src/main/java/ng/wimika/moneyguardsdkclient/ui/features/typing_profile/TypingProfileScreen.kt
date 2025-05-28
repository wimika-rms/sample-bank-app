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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.text.InputType
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguardsdkclient.ui.LocalToken
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp

private const val TYPING_PROFILE_INPUT_ID = 1001

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingProfileScreen(
    viewModel: TypingProfileViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val token = LocalToken.current ?: ""
    var text by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var showValidationDialog by remember { mutableStateOf(false) }
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var editText by remember { mutableStateOf<EditText?>(null) }
    
    // Get user's first name from preferences
    val firstName = MoneyGuardClientApp.preferenceManager?.getUserFirstName() ?: "User"
    val expectedText = "hello, my name is $firstName"

    // Start the typing profile service when EditText is created
    LaunchedEffect(editText) {
        editText?.let {
            val sdk = MoneyGuardSdk.initialize(context)
            val typingProfile = sdk.getTypingProfile()
            typingProfile.startService(context as android.app.Activity, intArrayOf(TYPING_PROFILE_INPUT_ID))
        }
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
                            text = "Please type the following text:",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = expectedText,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )

                        AndroidView(
                            factory = { context ->
                                EditText(context).apply {
                                    id = TYPING_PROFILE_INPUT_ID
                                    hint = "Type the text above"
                                    setText(text)
                                    // Disable auto-complete, predictive text, and emojis
                                    inputType = InputType.TYPE_CLASS_TEXT or 
                                              InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                                              InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                    addTextChangedListener(object : TextWatcher {
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                        override fun afterTextChanged(s: Editable?) {
                                            text = s?.toString() ?: ""
                                        }
                                    })
                                    editText = this
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (text.isNotEmpty() && token.isNotEmpty()) {
                                    if (text.trim() == expectedText.trim()) {
                                        viewModel.matchTypingProfile(text, context, token)
                                        showResultDialog = true
                                        // Clear text
                                        text = ""
                                        editText?.setText("")
                                    } else {
                                        showValidationDialog = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = text.isNotEmpty() && token.isNotEmpty() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Enroll Typing Profile")
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

    // Validation Dialog
    if (showValidationDialog) {
        AlertDialog(
            onDismissRequest = { 
                showValidationDialog = false
                // Clear text
                text = ""
                editText?.setText("")
            },
            title = { Text("Text Mismatch") },
            text = { Text("Please type the exact text shown above") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showValidationDialog = false
                        // Clear text
                        text = ""
                        editText?.setText("")
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Result Dialog
    if (showResultDialog && result != null) {
        AlertDialog(
            onDismissRequest = { 
                showResultDialog = false
                // Clear text and reset service
                text = ""
                editText?.setText("")
                val sdk = MoneyGuardSdk.initialize(context)
                val typingProfile = sdk.getTypingProfile()
                typingProfile.resetService()
            },
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
                TextButton(
                    onClick = { 
                        showResultDialog = false
                        // Clear text and reset service
                        text = ""
                        editText?.setText("")
                        val sdk = MoneyGuardSdk.initialize(context)
                        val typingProfile = sdk.getTypingProfile()
                        typingProfile.resetService()
                    }
                ) {
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