package ng.wimika.moneyguardsdkclient.ui.features.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguard_sdk.services.utility.MoneyGuardUtility
import ng.wimika.moneyguardsdkclient.LocalMoneyGuardUtility
import ng.wimika.moneyguardsdkclient.ui.features.landing.FeatureCategory
import ng.wimika.moneyguardsdkclient.ui.features.login.LoginViewModel

@Serializable
object Dashboard

@Composable
fun DashboardDestination(
    viewModel: LoginViewModel = viewModel(),
    onUtilitiesClick: () -> Unit,
    onLogout: () -> Unit,
    onEnableMoneyGuard: () -> Unit
) {
    DashboardScreen(
        onUtilitiesClick = onUtilitiesClick,
        onLogout = {
            viewModel.logOut()
            onLogout()
        },
        onEnableMoneyGuard = onEnableMoneyGuard
    )
}

@Composable
fun DashboardScreen(
    onUtilitiesClick: () -> Unit,
    onLogout: () -> Unit,
    onEnableMoneyGuard: () -> Unit
) {
    val sdkUtils: MoneyGuardUtility? = LocalMoneyGuardUtility.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = onLogout,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout"
                    )
                }
            }

            FeatureCategory(
                title = "Enable MoneyGuard",
                icon = Icons.Default.Add,
                onClick = onEnableMoneyGuard
            )

            FeatureCategory(
                title = "Utilities",
                icon = Icons.Default.Settings,
                onClick = onUtilitiesClick
            )
        }
    }
} 