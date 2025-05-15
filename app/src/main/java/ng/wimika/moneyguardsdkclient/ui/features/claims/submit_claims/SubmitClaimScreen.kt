package ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import java.io.File
import ng.wimika.moneyguardsdkclient.utils.PermissionUtils
import java.sql.Date
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ng.wimika.moneyguard_sdk_commons.utils.DateUtils
import ng.wimika.moneyguardsdkclient.ui.features.claims.widgets.ClaimsPolicyAccountsSelectionCard
import ng.wimika.moneyguardsdkclient.utils.FileUtils

@Serializable
object SubmitClaim

@Composable
fun SubmitClaimDestination(
    onBackPressed: () -> Unit = {},
    viewModel: SubmitViewModel = viewModel(),
) {
    val state by viewModel.submitClaimState.collectAsStateWithLifecycle()

    SubmitClaimScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onBackPressed = onBackPressed
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitClaimScreen(
    state: SubmitClaimState,
    onEvent: (SubmitClaimEvent) -> Unit,
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            android.util.Log.d("SubmitClaim", "Content URI: $uri")
            android.util.Log.d("SubmitClaim", "Direct MIME type: ${context.contentResolver.getType(uri)}")
        }
        val files = uris.map { uri ->
            File(uri.path ?: "")
        }
        onEvent(SubmitClaimEvent.OnFilesSelected(files))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        if (allGranted) {
            filePickerLauncher.launch("*/*")
        } else {
            onEvent(SubmitClaimEvent.ShowPermissionRationale)
        }
    }

    if (state.showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { onEvent(SubmitClaimEvent.HidePermissionRationale) },
            title = { Text("Permission Required") },
            text = { Text("Storage permission is required to select files for your claim.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(SubmitClaimEvent.HidePermissionRationale)
                        activity?.let {
                            PermissionUtils.requestPermissions(it, permissions, 100)
                        }
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SubmitClaimEvent.HidePermissionRationale) }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.lossDate?.time ?: System.currentTimeMillis()
        )

        AlertDialog(
            onDismissRequest = { onEvent(SubmitClaimEvent.HideDatePicker) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    title = { Text("Select Loss Date") },
                    headline = { Text("Select Loss Date") },
                    showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onEvent(SubmitClaimEvent.HideDatePicker)
                        }
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                onEvent(SubmitClaimEvent.LossDateChanged(Date(millis)))
                            }
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Claim") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message Display
            state.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            //Select Account
            ClaimsPolicyAccountsSelectionCard(
                accounts = state.accounts,
                selectedAccount = state.selectedAccount,
                onAccountSelected = { account ->
                    onEvent(SubmitClaimEvent.AccountSelected(account))
                }
            )

            // Incident Name
            OutlinedTextField(
                value = state.nameofIncident,
                onValueChange = { onEvent(SubmitClaimEvent.NameOfIncidentChanged(it)) },
                label = { Text("Name of Incident") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Loss Amount
            OutlinedTextField(
                value = state.lossAmount.toString(),
                onValueChange = {
                    val amount = it.toDoubleOrNull() ?: 0.0
                    onEvent(SubmitClaimEvent.LossAmountChanged(amount))
                },
                label = { Text("Loss Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Loss Date
            OutlinedTextField(
                value = state.lossDate?.let {
                    DateUtils.formatDate(it)
//                    LocalDate.ofInstant(it.toInstant(), ZoneId.systemDefault())
//                        .format(DateTimeFormatter.ISO_DATE)
                } ?: "",
                onValueChange = { },
                label = { Text("Loss Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { onEvent(SubmitClaimEvent.ShowDatePicker) }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )

            // Statement
            OutlinedTextField(
                value = state.statement,
                onValueChange = { onEvent(SubmitClaimEvent.StatementChanged(it)) },
                label = { Text("Statement") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // File Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Attachments",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        onClick = {
                            if (PermissionUtils.arePermissionsGranted(context, permissions)) {
                                filePickerLauncher.launch("*/*")
                            } else {
                                permissionLauncher.launch(permissions)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Files"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Files")
                    }

                    if (state.selectedFiles.isNotEmpty()) {
                        Text(
                            text = "${state.selectedFiles.size} files selected",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.selectedFiles.forEachIndexed { index, file ->
                                Card(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box {
                                        val isImage = FileUtils.isFileAnImage(context, file)

                                        if (isImage) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(file)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Selected file $index",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .border(
                                                        width = 1.dp,
                                                        shape = RoundedCornerShape(8.dp),
                                                        brush = Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                            ),
                                                            start = Offset.Zero,
                                                            end = Offset(20f, 0f)
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = when {
                                                            FileUtils.isFileAVideo(context, file) -> Icons.Default.PlayArrow
                                                            FileUtils.isFileAnAudio(context, file) -> Icons.Default.PlayArrow
                                                            else -> Icons.Default.Info
                                                        },
                                                        contentDescription = "File type icon",
                                                        modifier = Modifier.size(32.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = when {
                                                            FileUtils.isFileAVideo(context, file) -> "Video"
                                                            FileUtils.isFileAnAudio(context, file) == true -> "Audio"
                                                            else -> "Document"
                                                        },
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                val updatedFiles =
                                                    state.selectedFiles.filterIndexed { i, _ -> i != index }
                                                onEvent(
                                                    SubmitClaimEvent.OnFilesSelected(
                                                        updatedFiles
                                                    )
                                                )
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove file",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    onEvent(SubmitClaimEvent.SubmitClaim)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                enabled = state.shouldEnableButton
            ) {

                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                    )
                }

                if (!state.isLoading) {
                    Text("Submit Claim")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SubmitClaimScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SubmitClaimScreen(
                state = SubmitClaimState(),
                onEvent = {},
                onBackPressed = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark Mode")
@Composable
fun SubmitClaimScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SubmitClaimScreen(
                state = SubmitClaimState(),
                onEvent = {},
                onBackPressed = {}
            )
        }
    }
}