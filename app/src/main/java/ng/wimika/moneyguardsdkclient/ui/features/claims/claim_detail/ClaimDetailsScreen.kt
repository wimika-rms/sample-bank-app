package ng.wimika.moneyguardsdkclient.ui.features.claims.claim_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimResponse
import ng.wimika.moneyguard_sdk.services.claims.datasource.model.Note
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
import ng.wimika.moneyguardsdkclient.utils.DateUtils
import org.joda.time.DateTime

@Serializable
data class ClaimDetail(
    val claimId: Int
)


@Composable
fun ClaimDetailDestination(
    claimId: Int,
    onBackPressed: () -> Unit,
    viewModel: ClaimDetailsViewModel = viewModel()
) {

    val state by viewModel.claimDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(claimId) {
        viewModel.onEvent(ClaimDetailsEvent.LoadClaim(claimId))
    }

    ClaimDetailsScreen(
        state = state,
        onBackPressed = onBackPressed
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailsScreen(
    state: ClaimDetailsState,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Claim Details") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage ?: "An error occurred",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.claim != null -> {
                    ClaimDetailsContent(
                        claim = state.claim!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClaimDetailsContent(
    claim: ClaimResponse,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = claim.status ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Claim Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Claim Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Claim ID", claim.id.toString())
                InfoRow("Policy ID", claim.policyId.toString())
                InfoRow("Name", claim.name ?: claim.natureOfIncident)
                InfoRow("Brief", claim.brief ?: "")
                InfoRow("Loss Amount", "₦${claim.lossAmount}")
                InfoRow("Nature of Incident", claim.natureOfIncident)
                InfoRow("Statement", claim.statement ?: "")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dates
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Important Dates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Loss Date", formatDate(claim.lossDate))
                InfoRow("Report Date", formatDate(claim.reportDate))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Account Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Bank", claim.bank ?: "")
                InfoRow("Account Number", claim.account ?: "")
                InfoRow("Account ID", claim.accountId.toString())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes Section
        if (claim.notes.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    claim.notes.forEach { note ->
                        NoteItem(note)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Feedback Section
        if (claim.feedback?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Feedback",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = claim.feedback ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NoteItem(note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.type,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = formatDate(note.created),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

private fun formatDate(dateTime: String): String {
    return DateUtils.formatDate(dateTime)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            onBackPressed = {},
            state = ClaimDetailsState(
                isLoading = true
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenWithNotesPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            onBackPressed = {},
            state = ClaimDetailsState(
                claim = ClaimResponse(
                    id = 1,
                    policyId = 1,
                    name = "Test Claim",
                    brief = "Test brief description",
                    lossAmount = 1000.0,
                    natureOfIncident = "Test incident",
                    statement = "Test statement",
                    lossDate = "2024-03-20T00:00:00.000Z",
                    reportDate = "2024-03-20T00:00:00.000Z",
                    bank = "Test Bank",
                    account = "1234567890",
                    accountId = 1,
                    status = "Submitted",
                    notes = listOf(
                        Note(
                            type = "System",
                            note = "Test note",
                            created = "2024-03-20T00:00:00.000Z"
                        )
                    ),
                    feedback = "",
                    userId = "21"
                )
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenWithFeedbackPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            onBackPressed = {},
            state = ClaimDetailsState(
                claim = ClaimResponse(
                    id = 1,
                    policyId = 1,
                    name = "Test Claim",
                    brief = "Test brief description",
                    lossAmount = 1000.0,
                    natureOfIncident = "Test incident",
                    statement = "Test statement",
                    lossDate = "2024-03-20T00:00:00.000Z",
                    reportDate = "2024-03-20T00:00:00.000Z",
                    bank = "Test Bank",
                    account = "1234567890",
                    accountId = 1,
                    status = "Submitted",
                    notes = emptyList(),
                    feedback = "Test feedback message",
                    userId = "21"
                )
            )
        )
    }
}

