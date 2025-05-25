package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import android.content.Context
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import ng.wimika.moneyguardsdkclient.utils.LocationViewModel
import ng.wimika.moneyguardsdkclient.utils.LocationViewModelFactory


@Serializable
object CheckDebitTransaction


@Composable
fun CheckDebitTransactionDestination(
    viewModel: CheckDebitTransactionViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModelFactory(
            context = LocalContext.current,
            locationManager = LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        )
    ),
    onBackClick: () -> Unit
) {
    val state by viewModel.checkDebitState.collectAsStateWithLifecycle()
    val locationState by locationViewModel.locationState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        locationViewModel.getCurrentLocation()
    }

    LaunchedEffect(locationState) {
        locationState?.let { location ->
            viewModel.updateLocation(location)
        }
    }

    CheckDebitTransactionScreen(
        onBackClick = onBackClick,
        state = state,
        onEvent = viewModel::onEvent
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheckDebitTransactionScreen(
    onBackClick: () -> Unit,
    state: CheckDebitTransactionState,
    onEvent: (CheckDebitTransactionEvent) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Check Debit Transaction",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    value = state.amount.toString(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    onValueChange = { amount ->
                        if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                            onEvent(CheckDebitTransactionEvent.UpdateAmount(amount.toDouble()))
                        }
                    },
                    label = { Text("Amount") },
                )


                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    value = state.sourceAccountNumber,
                    onValueChange = { onEvent(CheckDebitTransactionEvent.UpdateSourceAccountNumber(it)) },
                    label = { Text("Source Account Number") },
                )

                OutlinedTextField(
                    value = state.destinationAccountNumber,
                    onValueChange = {
                        onEvent(
                            CheckDebitTransactionEvent.UpdateDestinationAccountNumber(
                                it
                            )
                        )
                    },
                    label = { Text(text = "Destination Account Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.destinationBank,
                    onValueChange = { onEvent(CheckDebitTransactionEvent.UpdateDestinationBank(it)) },
                    label = { Text("Destination Bank") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = state.memo,
                    onValueChange = { onEvent(CheckDebitTransactionEvent.UpdateMemo(it)) },
                    label = { Text("Memo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    minLines = 3
                )

                Button(
                    enabled = state.enableButton,
                    onClick = { onEvent(CheckDebitTransactionEvent.CheckDebitClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp)
                ) {

                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }

                    if (!state.isLoading) {
                        Text("Check Debit Transaction")
                    }
                }


                Box(modifier = Modifier.padding(top = 16.dp))

                Text("Current Location")
                Text(
                    text = "Longitude: ${state.geoLocation.lon}, Latitude: ${state.geoLocation.lat}"
                )
            }

            if (state.alertData.showAlert) {
                AlertDialog(
                    onDismissRequest = { onEvent(CheckDebitTransactionEvent.DismissAlert) },
                    title = { Text(state.alertData.title) },
                    text = { Text(state.alertData.message) },
                    confirmButton = {
                        TextButton(
                            onClick = { onEvent(CheckDebitTransactionEvent.DismissAlert) }
                        ) {
                            Text(state.alertData.buttonText)
                        }
                    },
                    dismissButton = state.alertData.secondaryButtonText?.let { secondaryText ->
                        {
                            TextButton(
                                onClick = { onEvent(CheckDebitTransactionEvent.DismissAlert) }
                            ) {
                                Text(secondaryText)
                            }
                        }
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun CheckDebitTransactionScreenPreview() {
    MaterialTheme {
        CheckDebitTransactionScreen(
            state = CheckDebitTransactionState(),
            onEvent = {},
            onBackClick = {}
        )
    }
}