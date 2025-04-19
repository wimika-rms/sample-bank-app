package ng.wimika.moneyguardsdkclient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguard_sdk.services.MoneyGuardSdkService
import ng.wimika.moneyguard_sdk.services.authentication.MoneyGuardAuthentication
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguardsdkclient.ui.navigation.NavigationHost
import ng.wimika.moneyguardsdkclient.ui.theme.MoneyGuardSdkClientTheme

class MainActivity : ComponentActivity() {

    private val sdkService: MoneyGuardSdkService? by lazy {
        MoneyGuardClientApp.sdkService
    }

    private val sdkUtils: MoneyGuardUtility? by lazy {
        sdkService?.utility()
    }

    private val sdkAuth: MoneyGuardAuthentication? by lazy {
        sdkService?.authentication()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalMoneyGuardUtility provides sdkUtils,
                LocalMoneyGuardAuthentication provides sdkAuth
            ) {
                MoneyGuardSdkClientTheme {
                    val navigationController = rememberNavController()
                    NavigationHost(navController = navigationController)
                }
            }

        }
    }
}

val LocalMoneyGuardUtility = staticCompositionLocalOf<MoneyGuardUtility?> {
    error("No MoneyGuardUtility provided")
}

val LocalMoneyGuardAuthentication = staticCompositionLocalOf<MoneyGuardAuthentication?> {
    error("No MoneyGuardAuthentication provided")
}

