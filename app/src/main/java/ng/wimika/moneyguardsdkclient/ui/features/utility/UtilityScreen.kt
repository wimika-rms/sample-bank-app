package ng.wimika.moneyguardsdkclient.ui.features.utility

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguardsdkclient.LocalMoneyGuardUtility


@Serializable
object Utility


@Composable
fun UtilityScreen(
    onNavigateToMoneyGuard: () -> Unit
) {
    val sdkUtils: MoneyGuardUtility? = LocalMoneyGuardUtility.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val appContext = LocalContext.current

            Button(onClick = onNavigateToMoneyGuard) {
                Text("Enable MoneyGuard")
            }

            Button(onClick = {
                val isAppInstalled = sdkUtils?.isMoneyGuardInstalled()
                Toast.makeText(
                    appContext,
                    "MoneyGuard Installed: $isAppInstalled",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
                Text("Check MoneyGuard Status")
            }

            Button(onClick = {
                sdkUtils?.launchAppInstallation()
            }) {
                Text("Install Moneyguard")
            }

            Button(onClick = {
                sdkUtils?.launchMoneyGuardApp()
            }) {
                Text("Launch MoneyGuard Standalone App")
            }


        }
    }
}