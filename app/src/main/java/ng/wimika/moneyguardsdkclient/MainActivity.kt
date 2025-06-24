package ng.wimika.moneyguardsdkclient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.compose.rememberNavController
import ng.wimika.moneyguard_sdk.services.MoneyGuardSdkService
import ng.wimika.moneyguard_sdk.services.authentication.MoneyGuardAuthentication
import ng.wimika.moneyguard_sdk.services.onboarding_info.OnboardingInfo
import ng.wimika.moneyguard_sdk.services.policy.MoneyGuardPolicy
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
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

    private val sdkPolicy: MoneyGuardPolicy? by lazy {
        sdkService?.policy()
    }

    private val sdkOnboardingInfo: OnboardingInfo? by lazy {
        sdkService?.onboardingInfo()
    }

    private val preferenceManager: IPreferenceManager? by lazy {
        MoneyGuardClientApp.preferenceManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isLoggedIn by remember { mutableStateOf(preferenceManager?.getMoneyGuardToken()?.isNotEmpty() == true) }
            var token by remember { mutableStateOf(preferenceManager?.getMoneyGuardToken() ?: "") }

            // Observe token changes
            LaunchedEffect(Unit) {
                while (true) {
                    val currentToken = preferenceManager?.getMoneyGuardToken() ?: ""
                    if (currentToken != token) {
                        token = currentToken
                        isLoggedIn = currentToken.isNotEmpty()
                    }
                    kotlinx.coroutines.delay(100) // Check every 100ms
                }
            }

            CompositionLocalProvider(
                LocalMoneyGuardUtility provides sdkUtils,
                LocalMoneyGuardAuthentication provides sdkAuth,
                LocalMoneyGuardOnboardingInfo provides sdkOnboardingInfo
            ) {
                MoneyGuardSdkClientTheme {
                    val navigationController = rememberNavController()
                    NavigationHost(
                        navController = navigationController,
                        isLoggedIn = isLoggedIn,
                        moneyGuardPolicy = sdkPolicy ?: throw IllegalStateException("MoneyGuardPolicy not available"),
                        token = token
                    )
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

val LocalMoneyGuardOnboardingInfo = staticCompositionLocalOf<OnboardingInfo?> {
    error("No OnboardingInfo provided")
}

