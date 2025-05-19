package ng.wimika.moneyguardsdkclient.ui.features.dashboard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.widgets.AccountDetailsCard
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.widgets.QuickActionsCard
import ng.wimika.moneyguardsdkclient.ui.features.login.LoginViewModel
import ng.wimika.moneyguardsdkclient.utils.PermissionUtils

@Serializable
object Dashboard

@Composable
fun DashboardDestination(
    viewModel: LoginViewModel = viewModel(),
    onUtilitiesClick: () -> Unit,
    onEnableMoneyGuard: () -> Unit,
    onDebitCheckClick: () -> Unit,
    onClaimClick: () -> Unit,
    onLogout: () -> Unit
) {
    DashboardScreen(
        onUtilitiesClick = onUtilitiesClick,
        onDebitCheckClick = onDebitCheckClick,
        onLogout = {
            viewModel.logOut()
            onLogout()
        },
        onEnableMoneyGuard = onEnableMoneyGuard,
        onClaimClick = onClaimClick
    )
}

@Composable
fun DashboardScreen(
    onUtilitiesClick: () -> Unit,
    onEnableMoneyGuard: () -> Unit,
    onDebitCheckClick: () -> Unit,
    onClaimClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var hasLocationPermissions by remember {
        mutableStateOf(
            PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    && PermissionUtils.isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermissions = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermissions) {
            onDebitCheckClick()
        }
    }

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
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                }
            }

            AccountDetailsCard()

            Box(modifier = Modifier.height(16.dp))

            QuickActionsCard(
                title = "Quick Actions",
                onUtilityClick = onUtilitiesClick,
                onDebitCheckClick = {
                    if (hasLocationPermissions) {
                        onDebitCheckClick()
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onClaimsClick = onClaimClick
            )
        }
    }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        onDebitCheckClick = {},
        onUtilitiesClick = {},
        onLogout = {},
        onEnableMoneyGuard = {},
        onClaimClick = {}
    )
}