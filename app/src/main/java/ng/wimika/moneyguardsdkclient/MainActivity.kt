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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguardsdkclient.ui.theme.MoneyGuardSdkClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyGuardSdkClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val appContext = LocalContext.current

                        Button(onClick = {
                            val isAppInstalled = MoneyGuardSdk.isMoneyGuardInstalled()
                            Toast.makeText(
                                appContext,
                                "MoneyGuard Installed: $isAppInstalled",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text("Check MoneyGuard App Installation")
                        }

                        Button(onClick = {
                            MoneyGuardSdk.installApp()
                        }) {
                            Text("Launch MoneyGuard App installation")
                        }

                        Button(onClick = {
                            MoneyGuardSdk.launchMoneyGuardApp()
                        }) {
                            Text("Launch MoneyGuard App")
                        }
                    }
                }
            }
        }
    }
}

