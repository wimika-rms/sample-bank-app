package ng.wimika.moneyguardsdkclient.ui.features.claims.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimResponse
import java.text.NumberFormat
import java.util.*

@Composable
fun ClaimItemCard(
    claim: ClaimResponse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header with claim name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = claim.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(status = claim.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Brief description
            Text(
                text = claim.brief,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Loss amount and dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Loss Amount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatCurrency(claim.lossAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Loss Date",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = claim.lossDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Bank and account info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${claim.bank} - ${claim.account}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "approved" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        "pending" -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        "rejected" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

private fun formatCurrency(amount: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG"))
    return formatter.format(amount)
}

@Preview(showBackground = true)
@Composable
fun ClaimItemCardPreview() {
    val sampleClaim = ClaimResponse(
        id = 1,
        policyId = 123,
        lossDate = "2024-03-15",
        reportDate = "2024-03-16",
        name = "Phone Theft Claim",
        brief = "iPhone 13 Pro Max was stolen during a robbery incident",
        lossAmount = 750000,
        status = "Pending",
        statement = "The incident occurred at 8:30 PM",
        userId = "user123",
        bank = "Access Bank",
        accountId = 456,
        account = "0123456789",
        feedback = "Under review",
        natureOfIncident = "Theft",
        notes = emptyList()
    )

    MaterialTheme {
        ClaimItemCard(
            claim = sampleClaim,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClaimItemCardApprovedPreview() {
    val sampleClaim = ClaimResponse(
        id = 2,
        policyId = 124,
        lossDate = "2024-03-10",
        reportDate = "2024-03-11",
        name = "Laptop Damage Claim",
        brief = "MacBook Pro screen damaged due to water spillage",
        lossAmount = 450000,
        status = "Approved",
        statement = "Accidental damage occurred while working",
        userId = "user124",
        bank = "First Bank",
        accountId = 457,
        account = "9876543210",
        feedback = "Claim approved for processing",
        natureOfIncident = "Accidental Damage",
        notes = emptyList()
    )

    MaterialTheme {
        ClaimItemCard(
            claim = sampleClaim,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClaimItemCardRejectedPreview() {
    val sampleClaim = ClaimResponse(
        id = 3,
        policyId = 125,
        lossDate = "2024-03-05",
        reportDate = "2024-03-06",
        name = "Watch Loss Claim",
        brief = "Rolex watch lost during travel",
        lossAmount = 1200000,
        status = "Rejected",
        statement = "Watch was not properly secured",
        userId = "user125",
        bank = "UBA",
        accountId = 458,
        account = "1122334455",
        feedback = "Claim rejected due to insufficient documentation",
        natureOfIncident = "Loss",
        notes = emptyList()
    )

    MaterialTheme {
        ClaimItemCard(
            claim = sampleClaim,
            modifier = Modifier.padding(16.dp)
        )
    }
}

