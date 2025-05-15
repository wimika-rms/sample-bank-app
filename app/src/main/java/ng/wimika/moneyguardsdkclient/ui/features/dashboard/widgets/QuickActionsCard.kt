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


@Composable
fun QuickActionsCard(
    title: String,
    onUtilityClick: () -> Unit,
    onDebitCheckClick: () -> Unit,
    onClaimsClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                QuickAction(
                    icon = Icons.Default.Settings,
                    label = "Utility",
                    onClick = onUtilityClick,
                    modifier = Modifier.weight(1f)
                )
                QuickAction(
                    icon = Icons.Default.Info,
                    label = "Debit Check",
                    onClick = onDebitCheckClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                QuickAction(
                    icon = Icons.Default.Email,
                    label = "Claims",
                    onClick = onClaimsClick,
                    modifier = Modifier.weight(1f)
                )
//                QuickAction(
//                    icon = Icons.Default.Info,
//                    label = "Debit Check",
//                    onClick = onDebitCheckClick,
//                    modifier = Modifier.weight(1f)
//                )
            }
        }

    }
}


@Composable
fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(vertical = 24.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            onClaimsClick = {}
        )
    }
}


@Preview
@Composable
private fun QuickActionPreview() {
    MaterialTheme {
        QuickAction(
            icon = Icons.AutoMirrored.Filled.Send,
            label = "Transfer",
            onClick = { /* TODO: Handle Transfer */ }
        )
    }
}