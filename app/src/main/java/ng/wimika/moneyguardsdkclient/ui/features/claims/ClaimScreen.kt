package ng.wimika.moneyguardsdkclient.ui.features.claims

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import ng.wimika.moneyguardsdkclient.ui.features.claims.widgets.ClaimItemCard


@Serializable
object Claim


@Composable
fun ClaimDestination(
    onBackClick: () -> Unit,
    addClaimsClick: () -> Unit,
    onClaimItemClick: (id: Int) -> Unit
) {
    ClaimScreen(
        onBackClick = onBackClick,
        addClaimsClick = addClaimsClick,
        state = ClaimListState(),
        onClaimItemClick = onClaimItemClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimScreen(
    onBackClick: () -> Unit,
    addClaimsClick: () -> Unit,
    onClaimItemClick: (id: Int) -> Unit,
    state: ClaimListState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Claims")
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable(
                            enabled = true,
                            role = Role.Button,
                            onClick = onBackClick
                        ),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = addClaimsClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Back"
                )
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            if (!state.isLoading) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items (10) {
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

                        ClaimItemCard(
                            claim = sampleClaim,
                            modifier = Modifier.padding(16.dp),
                            onClick = { onClaimItemClick(sampleClaim.id) }
                        )
                    }
                }
            }
        }
    }
}