package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType


@Serializable
object CheckDebitTransaction


sealed class CheckDebitTransactionEvent {
    object CheckDebitClick : CheckDebitTransactionEvent()
    data class UpdateAmount(val value: Double) : CheckDebitTransactionEvent()
    data class UpdateSourceAccountNumber(val value: String) : CheckDebitTransactionEvent()
    data class UpdateDestinationAccountNumber(val value: String) : CheckDebitTransactionEvent()
    data class UpdateDestinationBank(val value: String) : CheckDebitTransactionEvent()
    data class UpdateMemo(val value: String) : CheckDebitTransactionEvent()
}


@Composable
fun CheckDebitTransactionDestination(
    viewModel: CheckDebitTransactionViewModel = viewModel(
        factory = CheckDebitTransactionViewModelFactory(
            context = LocalContext.current,
            locationManager = LocalContext.current.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        )
    )
) {
    val state by viewModel.checkDebitState.collectAsStateWithLifecycle()

    CheckDebitTransactionScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}


@Composable
private fun CheckDebitTransactionScreen(
    state: CheckDebitTransactionState,
    onEvent: (CheckDebitTransactionEvent) -> Unit
) {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Check Debit Transaction",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = state.sourceAccountNumber,
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
    }

}


@Preview
@Composable
private fun CheckDebitTransactionScreenPreview() {
    MaterialTheme {
        CheckDebitTransactionScreen(
            state = CheckDebitTransactionState(),
            onEvent = {}
        )
    }
}