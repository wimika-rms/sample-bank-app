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
import androidx.compose.ui.platform.LocalContext


@Serializable
object CheckDebitTransaction


sealed class CheckDebitTransactionEvent {
    object CheckDebitClick: CheckDebitTransactionEvent()
    data class UpdateSourceAccountNumber(val value: String): CheckDebitTransactionEvent()
    data class UpdateDestinationAccountNumber(val value: String): CheckDebitTransactionEvent()
    data class UpdateDestinationBank(val value: String): CheckDebitTransactionEvent()
    data class UpdateMemo(val value: String): CheckDebitTransactionEvent()
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
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Check Debit Transaction",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
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
            onValueChange = { onEvent(CheckDebitTransactionEvent.UpdateDestinationAccountNumber(it)) },
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
            onClick = { onEvent(CheckDebitTransactionEvent.CheckDebitClick) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Check Debit")
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