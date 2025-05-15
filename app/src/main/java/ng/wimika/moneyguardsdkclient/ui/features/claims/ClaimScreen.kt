package ng.wimika.moneyguardsdkclient.ui.features.claims

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguardsdkclient.ui.features.claims.widgets.ClaimItemCard
import ng.wimika.moneyguard_sdk.services.claims.datasource.model.ClaimStatus
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.setValue


@Serializable
object Claim


@Composable
fun ClaimDestination(
    onBackClick: () -> Unit,
    addClaimsClick: () -> Unit,
    onClaimItemClick: (id: Int) -> Unit,
    viewModel: ClaimViewModel = viewModel()
) {
    val state by viewModel.claimState.collectAsStateWithLifecycle()


    ClaimScreen(
        onBackClick = onBackClick,
        addClaimsClick = addClaimsClick,
        state = state,
        onClaimItemClick = onClaimItemClick,
        onEvent = viewModel::onEvent
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimScreen(
    onBackClick: () -> Unit,
    addClaimsClick: () -> Unit,
    onClaimItemClick: (id: Int) -> Unit,
    state: ClaimListState,
    onEvent: (ClaimEvent) -> Unit,
) {
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Claims") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Filter Claims"
                            )
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            ClaimStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.name) },
                                    onClick = {
                                        onEvent(ClaimEvent.OnFilterSelected(status))
                                        showFilterMenu = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = if (state.status == status) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
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
        Box(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }


            if (state.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(state.errorMessage)
                }
            }

            if (state.claims.isEmpty() && state.errorMessage == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("You don't have any claims")
                }
            }

            if (!state.isLoading && state.errorMessage == null) {
                LazyColumn(modifier = Modifier) {

                    itemsIndexed(state.claims){ index, claim ->
                        ClaimItemCard(
                            claim = claim,
                            modifier = Modifier.padding(16.dp),
                            onClick = { onClaimItemClick(claim.id) }
                        )
                    }

                }
            }
        }
    }
}