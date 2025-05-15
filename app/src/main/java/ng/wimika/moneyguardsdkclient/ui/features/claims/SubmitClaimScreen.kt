package ng.wimika.moneyguardsdkclient.ui.features.claims

import android.Manifest
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ng.wimika.moneyguardsdkclient.utils.PermissionUtils

@Serializable
data class SubmitClaimRequest(
    val id: Int = 0,
    val accountId: Long = 0L,
    val lossDate: String = "",
    val nameOfIncident: String = "",
    val lossAmount: Double = 0.0,
    val statement: String = ""
)

@Serializable
object SubmitClaim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitClaimScreen(
    onBackPressed: () -> Unit = {},
    onSubmit: (SubmitClaimRequest, List<Any>) -> Unit = { _, _ -> }
) {
    var nameOfIncident by remember { mutableStateOf("") }
    var lossAmount by remember { mutableStateOf("") }
    var statement by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    
    val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
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
        selectedFiles = uris
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        if (allGranted) {
            filePickerLauncher.launch("*/*")
        } else {
            showPermissionRationale = true
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permission Required") },
            text = { Text("Storage permission is required to select files for your claim.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationale = false
                        activity?.let {
                            PermissionUtils.requestPermissions(it, permissions, 100)
                        }
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        AlertDialog(
            onDismissRequest = { showDatePicker = false },
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
                            showDatePicker = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            }
                            showDatePicker = false
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
            // Incident Name
            OutlinedTextField(
                value = nameOfIncident,
                onValueChange = { nameOfIncident = it },
                label = { Text("Name of Incident") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Loss Amount
            OutlinedTextField(
                value = lossAmount,
                onValueChange = { lossAmount = it },
                label = { Text("Loss Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Loss Date
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ISO_DATE),
                onValueChange = { },
                label = { Text("Loss Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                }
            )

            // Statement
            OutlinedTextField(
                value = statement,
                onValueChange = { statement = it },
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

                    if (selectedFiles.isNotEmpty()) {
                        Text(
                            text = "${selectedFiles.size} files selected",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedFiles.forEachIndexed { index, file ->
                                Card(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(100.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box {
                                        val mimeType = context.contentResolver.getType(file)
                                        val isImage = mimeType?.startsWith("image/") == true
                                        
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
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = when {
                                                        mimeType?.startsWith("video/") == true -> Icons.Default.PlayArrow
                                                        mimeType?.startsWith("audio/") == true -> Icons.Default.PlayArrow
                                                        else -> Icons.Default.Info
                                                    },
                                                    contentDescription = "File type icon",
                                                    modifier = Modifier.size(32.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        
                                        IconButton(
                                            onClick = {
                                                selectedFiles = selectedFiles.filterIndexed { i, _ -> i != index }
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
                    val request = SubmitClaimRequest(
                        nameOfIncident = nameOfIncident,
                        lossAmount = lossAmount.toDoubleOrNull() ?: 0.0,
                        lossDate = selectedDate.format(DateTimeFormatter.ISO_DATE),
                        statement = statement
                    )
                    onSubmit(request, selectedFiles)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = nameOfIncident.isNotBlank() && lossAmount.isNotBlank() && statement.isNotBlank()
            ) {
                Text("Submit Claim")
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
            SubmitClaimScreen()
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
            SubmitClaimScreen()
        }
    }
}