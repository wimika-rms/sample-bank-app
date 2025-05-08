package ng.wimika.moneyguardsdkclient.ui.features.checkdebit

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
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
import ng.wimika.moneyguard_sdk.services.authentication.MoneyGuardAuthentication
import ng.wimika.moneyguard_sdk.services.transactioncheck.TransactionCheck
import ng.wimika.moneyguard_sdk.services.transactioncheck.models.DebitTransaction
import ng.wimika.moneyguard_sdk.services.transactioncheck.models.LatLng
import ng.wimika.moneyguardsdkclient.MoneyGuardClientApp
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.models.GeoLocation
import ng.wimika.moneyguardsdkclient.utils.PermissionUtils

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

    private val transactionCheck: TransactionCheck? by lazy {
        MoneyGuardClientApp.sdkService?.transactionCheck()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _checkDebitState.update { currentState ->
                currentState.copy(
                    geoLocation = GeoLocation(
                        lat = location.latitude,
                        lon = location.longitude,
                        accuracy = location.accuracy
                    ),
                )
            }
            locationManager.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                if (PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    && PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    var location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    if (location != null) {
                        _checkDebitState.update { currentState ->
                            currentState.copy(
                                geoLocation = GeoLocation(
                                    lat = location.latitude,
                                    lon = location.longitude,
                                    accuracy = location.accuracy
                                ),
                            )
                        }
                    } else {

                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0L,
                                0f,
                                locationListener,
                                Looper.getMainLooper()
                            )
                        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0L,
                                0f,
                                locationListener,
                                Looper.getMainLooper()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _checkDebitState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
        }
    }

    fun onEvent(event: CheckDebitTransactionEvent) {
        when (event) {
            CheckDebitTransactionEvent.CheckDebitClick -> {
                val currentState = _checkDebitState.value
                val transactionData = TransactionData(
                    amount = currentState.amount,
                    sourceAccountNumber = currentState.sourceAccountNumber,
                    destinationAccountNumber = currentState.destinationAccountNumber,
                    destinationBank = currentState.destinationBank,
                    memo = currentState.memo,
                    geoLocation = currentState.geoLocation
                )

                checkDebitTransaction(transactionData)
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

            is CheckDebitTransactionEvent.UpdateAmount -> {
                _checkDebitState.update { currentState ->
                    currentState.copy(amount = event.value)
                }
            }
        }

        shouldEnableButton()
    }

    private fun checkDebitTransaction(data: TransactionData) {
        val sessionToken = preferenceManager?.getMoneyGuardToken() ?: ""
        val debitTransaction = DebitTransaction(
            sourceAccountNumber = data.sourceAccountNumber,
            destinationAccountNumber = data.destinationAccountNumber,
            destinationBank = data.destinationBank,
            memo = data.memo,
            amount = data.amount,
            location = LatLng(
                longitude = data.geoLocation.lon,
                latitude = data.geoLocation.lat
            )
        )

        transactionCheck?.checkDebitTransaction(sessionToken, debitTransaction,
            onSuccess = {},
            onFailure = {}
        )
    }

    private fun shouldEnableButton() {
        val currentState = _checkDebitState.value
        val shouldEnableButton = currentState.sourceAccountNumber.isNotEmpty() &&
                currentState.destinationAccountNumber.isNotEmpty() &&
                currentState.destinationBank.isNotEmpty() &&
                currentState.memo.isNotEmpty() && currentState.amount > 0.0 &&
                !currentState.isLoading

        _checkDebitState.update { currentState ->
            currentState.copy(enableButton = shouldEnableButton)
        }
    }
}

data class TransactionData(
    val amount: Double,
    val sourceAccountNumber: String,
    val destinationAccountNumber: String,
    val destinationBank: String,
    val memo: String,
    val geoLocation: GeoLocation
)