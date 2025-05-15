package ng.wimika.moneyguardsdkclient.ui.features.claims

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class ClaimResponse(
    val id: Int,
    val policyId: Int,
    val lossDate: String,
    val reportDate: String,
    val name: String,
    val brief: String,
    val lossAmount: Int,
    val status: String,
    val statement: String,
    val userId: String,
    val bank: String,
    val accountId: Int,
    val account: String,
    val feedback: String,
    val natureOfIncident: String,
    val notes: List<Note>
)

data class Note(
    val created: String,
    val note: String,
    val type: String
)

@Serializable
data class ClaimDetail(
    val claimId: Int
)

object MockClaimProvider {
    fun getAllClaims(): List<ClaimResponse> = listOf(
        ClaimResponse(
            id = 1,
            policyId = 67890,
            lossDate = "2024-03-15T10:30:00.000Z",
            reportDate = "2024-03-15T11:00:00.000Z",
            name = "John Doe",
            brief = "Vehicle accident on Lagos-Ibadan expressway",
            lossAmount = 500000,
            status = "Under Review",
            statement = "I was involved in a minor accident while driving to work. The other driver was at fault.",
            userId = "USER123",
            bank = "First Bank",
            accountId = 98765,
            account = "0123456789",
            feedback = "",
            natureOfIncident = "Vehicle Accident",
            notes = listOf(
                Note(
                    created = "2024-03-15T12:00:00.000Z",
                    note = "Initial assessment completed. Waiting for police report.",
                    type = "Assessment"
                )
            )
        ),
        ClaimResponse(
            id = 2,
            policyId = 67891,
            lossDate = "2024-03-14T09:15:00.000Z",
            reportDate = "2024-03-14T09:30:00.000Z",
            name = "Jane Smith",
            brief = "Home burglary incident",
            lossAmount = 750000,
            status = "Approved",
            statement = "My house was broken into while I was at work. Several valuable items were stolen.",
            userId = "USER124",
            bank = "Access Bank",
            accountId = 98766,
            account = "9876543210",
            feedback = "Your claim has been approved. The settlement amount will be processed within 3-5 business days.",
            natureOfIncident = "Burglary",
            notes = listOf(
                Note(
                    created = "2024-03-14T10:00:00.000Z",
                    note = "Police report received and verified.",
                    type = "Assessment"
                ),
                Note(
                    created = "2024-03-14T14:30:00.000Z",
                    note = "Claim approved after review of evidence.",
                    type = "Update"
                )
            )
        ),
        ClaimResponse(
            id = 3,
            policyId = 67892,
            lossDate = "2024-03-13T15:45:00.000Z",
            reportDate = "2024-03-13T16:00:00.000Z",
            name = "Michael Johnson",
            brief = "Mobile phone theft",
            lossAmount = 250000,
            status = "Pending",
            statement = "My phone was stolen while I was in a crowded market.",
            userId = "USER125",
            bank = "UBA",
            accountId = 98767,
            account = "2345678901",
            feedback = "",
            natureOfIncident = "Theft",
            notes = listOf(
                Note(
                    created = "2024-03-13T16:30:00.000Z",
                    note = "Police report pending.",
                    type = "Assessment"
                )
            )
        )
    )

    fun getClaimById(id: Int): ClaimResponse {
        return getAllClaims().find { it.id == id }
            ?: throw IllegalArgumentException("Claim with ID $id not found")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimDetailsScreen(
    claim: ClaimResponse,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
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
                        text = claim.status,
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
                    InfoRow("Name", claim.name)
                    InfoRow("Brief", claim.brief)
                    InfoRow("Loss Amount", "₦${claim.lossAmount}")
                    InfoRow("Nature of Incident", claim.natureOfIncident)
                    InfoRow("Statement", claim.statement)
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
                    
                    InfoRow("Bank", claim.bank)
                    InfoRow("Account Number", claim.account)
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
            if (claim.feedback.isNotEmpty()) {
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
                            text = claim.feedback,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
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

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            claim = MockClaimProvider.getClaimById(1),
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenWithNotesPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            claim = MockClaimProvider.getClaimById(2),
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClaimDetailsScreenWithFeedbackPreview() {
    MaterialTheme {
        ClaimDetailsScreen(
            claim = MockClaimProvider.getClaimById(2),
            onBackPressed = {}
        )
    }
}

