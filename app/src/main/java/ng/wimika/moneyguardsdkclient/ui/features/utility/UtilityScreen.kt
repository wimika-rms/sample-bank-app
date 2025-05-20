package ng.wimika.moneyguardsdkclient.ui.features.utility

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardAppStatus
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguardsdkclient.LocalMoneyGuardUtility
import ng.wimika.moneyguardsdkclient.ui.LocalToken

@Serializable
object Utility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityScreen(
    onNavigateToMoneyGuard: () -> Unit,
    onBack: () -> Unit
) {
    val sdkUtils: MoneyGuardUtility? = LocalMoneyGuardUtility.current
    val token = LocalToken.current
    val appContext = LocalContext.current
    var moneyGuardStatus by remember { mutableStateOf<MoneyGuardAppStatus?>(null) }
    val scope = rememberCoroutineScope()

    // Function to check MoneyGuard status
    fun checkStatus() {
        if (sdkUtils != null && token != null) {
            scope.launch {
                moneyGuardStatus = sdkUtils.checkMoneyguardStatus(token)
                Toast.makeText(
                    appContext,
                    "MoneyGuard Status: $moneyGuardStatus",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                appContext,
                "Please login to check MoneyGuard status",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Check status when screen is first loaded
    LaunchedEffect(Unit) {
        checkStatus()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MoneyGuard Utilities") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Show Enable MoneyGuard button if status is InActive or NoPolicyAppInstalled
            if (moneyGuardStatus == MoneyGuardAppStatus.InActive || 
                moneyGuardStatus == MoneyGuardAppStatus.NoPolicyAppInstalled) {
                Button(onClick = onNavigateToMoneyGuard) {
                    Text("Enable MoneyGuard")
                }
            }

            Button(onClick = { checkStatus() }) {
                Text("Check MoneyGuard Status")
            }

            // Show Install MoneyGuard button if status is InActive or ValidPolicyAppNotInstalled
            if (moneyGuardStatus == MoneyGuardAppStatus.InActive || 
                moneyGuardStatus == MoneyGuardAppStatus.ValidPolicyAppNotInstalled) {
                Button(onClick = {
                    sdkUtils?.launchAppInstallation()
                }) {
                    Text("Install Moneyguard")
                }
            }

            // Only show Launch MoneyGuard Standalone App button when status is Active
            if (moneyGuardStatus == MoneyGuardAppStatus.Active) {
                Button(onClick = {
                    sdkUtils?.launchMoneyGuardApp()
                }) {
                    Text("Launch MoneyGuard Standalone App")
                }
            }
        }
    }
}