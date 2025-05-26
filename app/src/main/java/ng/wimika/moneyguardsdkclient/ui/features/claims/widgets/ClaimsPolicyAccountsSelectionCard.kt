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
import ng.wimika.moneyguard_sdk.services.moneyguard_policy.models.BankAccount


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimsPolicyAccountsSelectionCard(
    accounts: List<BankAccount>,
    selectedAccount: BankAccount?,
    onAccountSelected: (BankAccount) -> Unit
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
                text = "Select Account",
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
                    value = selectedAccount?.let { "${it.bank} - ${it.number} (${it.type})" }
                        ?: "Select an account",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select account"
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
                    accounts.forEachIndexed { index, account ->
                        DropdownMenuItem(
                            text = {
                                Text("${account.bank} - ${account.number} (${account.type})")
                            },
                            onClick = {
                                onAccountSelected(account)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = if (account == selectedAccount)
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

@Preview(name = "Empty Accounts", showBackground = true)
@Composable
private fun ClaimsPolicyAccountsSelectionCardEmptyPreview() {
    MaterialTheme {
        ClaimsPolicyAccountsSelectionCard(
            accounts = emptyList(),
            selectedAccount = null,
            onAccountSelected = {}
        )
    }
}

@Preview(name = "With Accounts - None Selected", showBackground = true)
@Composable
private fun ClaimsPolicyAccountsSelectionCardWithAccountsPreview() {
    MaterialTheme {
        val accounts = listOf(
            BankAccount(
                id = 1,
                bank = "Access Bank",
                number = "0123456789",
                type = "Savings",
                name = "nv",
                defaultDebit = false,
                hasAcivePolicy = true
            ),
            BankAccount(
                id = 2,
                bank = "First Bank",
                number = "9876543210",
                type = "Current",
                name = "nv",
                defaultDebit = false,
                hasAcivePolicy = true
            )
        )

        ClaimsPolicyAccountsSelectionCard(
            accounts = accounts,
            selectedAccount = accounts[0],
            onAccountSelected = {}
        )
    }
}

@Preview(name = "With Accounts - First Selected", showBackground = true)
@Composable
private fun ClaimsPolicyAccountsSelectionCardWithSelectedAccountPreview() {
    MaterialTheme {
        val accounts = listOf(
            BankAccount(
                id = 1,
                bank = "Access Bank",
                number = "0123456789",
                type = "Savings",
                name = "nv",
                defaultDebit = false,
                hasAcivePolicy = true
            ),
            BankAccount(
                id = 2,
                bank = "First Bank",
                number = "9876543210",
                type = "Current",
                name = "nv",
                defaultDebit = false,
                hasAcivePolicy = true
            )
        )
        ClaimsPolicyAccountsSelectionCard(
            accounts = accounts,
            selectedAccount = accounts[0],
            onAccountSelected = {}
        )
    }
}