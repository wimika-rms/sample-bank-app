package ng.wimika.moneyguardsdkclient.ui.features.dashboard.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button

@Composable
fun QuickActionsCard(
    title: String,
    onUtilityClick: () -> Unit,
    onDebitCheckClick: () -> Unit,
    onClaimsClick: () -> Unit,
    onTypingProfileClick: () -> Unit,
    enableMoneyGuard: () -> Unit
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
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "Utilities",
                    icon = Icons.Default.Build,
                    onClick = onUtilityClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    text = "Debit Check",
                    icon = Icons.Default.CheckCircle,
                    onClick = onDebitCheckClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "Claims",
                    icon = Icons.Default.Add,
                    onClick = onClaimsClick,
                    modifier = Modifier.weight(1f)
                )

                QuickActionButton(
                    text = "Typing Profile",
                    icon = Icons.Default.Edit,
                    onClick = onTypingProfileClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    text = "Enable MoneyGuard",
                    icon = Icons.Default.Check,
                    onClick = enableMoneyGuard,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
private fun QuickActionsCardPreview() {
    MaterialTheme {
        QuickActionsCard(
            title = "Quick Actions",
            onUtilityClick = {},
            onDebitCheckClick = {},
            onClaimsClick = {},
            onTypingProfileClick = {},
            enableMoneyGuard = {}
        )
    }
}