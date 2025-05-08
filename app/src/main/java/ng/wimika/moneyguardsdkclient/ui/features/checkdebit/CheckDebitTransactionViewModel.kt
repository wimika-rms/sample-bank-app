package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation

class CheckDebitTransactionViewModelFactory(
    private val context: Context,
    private val locationManager: LocationManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckDebitTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckDebitTransactionViewModel(context, locationManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CheckDebitTransactionViewModel(
    private val context: Context,
    private val locationManager: LocationManager,
) : ViewModel() {

    private val _checkDebitState: MutableStateFlow<CheckDebitTransactionState> =
        MutableStateFlow(CheckDebitTransactionState())

    val checkDebitState: StateFlow<CheckDebitTransactionState> = _checkDebitState
        .onStart {
            getCurrentLocation()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CheckDebitTransactionState()
        )



    private fun getCurrentLocation() {
        viewModelScope.launch {
            _checkDebitState.update { currentState ->
                currentState.copy(isLoading = true)
            }

            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    var location: Location? =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                    if (location == null) {
                        location =
                            locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    }

                    if (location == null) {
                        location =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    }

                    if (location != null) {
                        _checkDebitState.update { currentState ->
                            currentState.copy(
                                geoLocation = GeoLocation(
                                    lat = location.latitude,
                                    lon = location.longitude,
                                    accuracy = location.accuracy.toDouble()
                                )
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun onEvent(event: CheckDebitTransactionEvent) {
        when (event) {
            CheckDebitTransactionEvent.CheckDebitClick -> {
                val currentState = _checkDebitState.value
                val transactionData = TransactionData(
                    sourceAccountNumber = currentState.sourceAccountNumber,
                    destinationAccountNumber = currentState.destinationAccountNumber,
                    destinationBank = currentState.destinationBank,
                    memo = currentState.memo,
                    geoLocation = currentState.geoLocation
                )
                // TODO: Use transactionData for further processing
            }

            is CheckDebitTransactionEvent.UpdateSourceAccountNumber -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(sourceAccountNumber = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateDestinationAccountNumber -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(destinationAccountNumber = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateDestinationBank -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(destinationBank = event.value)
                }
            }

            is CheckDebitTransactionEvent.UpdateMemo -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(memo = event.value)
                }
            }
        }
    }
}

data class TransactionData(
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val destinationBank: String,
    val memo: String,
    val geoLocation: GeoLocation
)