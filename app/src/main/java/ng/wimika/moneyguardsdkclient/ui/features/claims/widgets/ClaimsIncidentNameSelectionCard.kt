package ng.wimika.moneyguardsdkclient.ui.features.claims.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimsIncidentNameSelectionCard(
    incidentNames: List<String>,
    selectedIncidentName: String?,
    onIncidentNameSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Incident Name",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.clickable(
                    role = Role.Button,
                    onClick = { expanded = true }
                )
            ) {
                OutlinedTextField(
                    value = selectedIncidentName ?: "Select an incident name",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select incident name"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            role = Role.Button,
                            onClick = { expanded = true }
                        )
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    incidentNames.forEach { incidentName ->
                        DropdownMenuItem(
                            text = {
                                Text(incidentName)
                            },
                            onClick = {
                                onIncidentNameSelected(incidentName)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = if (incidentName == selectedIncidentName)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Empty Incident Names", showBackground = true)
@Composable
private fun ClaimsIncidentNameSelectionCardEmptyPreview() {
    MaterialTheme {
        ClaimsIncidentNameSelectionCard(
            incidentNames = emptyList(),
            selectedIncidentName = null,
            onIncidentNameSelected = {}
        )
    }
}

@Preview(name = "With Incident Names - None Selected", showBackground = true)
@Composable
private fun ClaimsIncidentNameSelectionCardWithNamesPreview() {
    MaterialTheme {
        val incidentNames = listOf(
            "Fire Damage",
            "Water Damage",
            "Theft",
            "Natural Disaster"
        )

        ClaimsIncidentNameSelectionCard(
            incidentNames = incidentNames,
            selectedIncidentName = null,
            onIncidentNameSelected = {}
        )
    }
}

@Preview(name = "With Incident Names - One Selected", showBackground = true)
@Composable
private fun ClaimsIncidentNameSelectionCardWithSelectedNamePreview() {
    MaterialTheme {
        val incidentNames = listOf(
            "Fire Damage",
            "Water Damage",
            "Theft",
            "Natural Disaster"
        )

        ClaimsIncidentNameSelectionCard(
            incidentNames = incidentNames,
            selectedIncidentName = "Fire Damage",
            onIncidentNameSelected = {}
        )
    }
}