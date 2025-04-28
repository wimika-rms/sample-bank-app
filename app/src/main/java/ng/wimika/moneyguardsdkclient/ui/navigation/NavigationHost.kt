package ng.wimika.moneyguardsdkclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.Dashboard
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardScreen
import ng.wimika.moneyguardsdkclient.ui.features.landing.Landing
import ng.wimika.moneyguardsdkclient.ui.features.landing.LandingScreen
import ng.wimika.moneyguardsdkclient.ui.features.login.Login
import ng.wimika.moneyguardsdkclient.ui.features.login.LoginDestination
import ng.wimika.moneyguardsdkclient.ui.features.utility.Utility
import ng.wimika.moneyguardsdkclient.ui.features.utility.UtilityScreen

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Landing
    ) {
        composable<Landing> {
            LandingScreen(
                gotoLoginClick = {
                    navController.navigate(Login)
                }
            )
        }

        composable<Login> {
            LoginDestination(
                onLoginSuccess = {
                    navController.navigate(Dashboard) {
                        popUpTo(Landing) { inclusive = true }
                    }
                }
            )
        }

        composable<Dashboard> {
            DashboardScreen(
                onUtilitiesClick = {
                    navController.navigate(Utility)
                },
                onLogout = {
                    navController.navigate(Landing) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                }
            )
        }

        composable<Utility> {
            UtilityScreen()
        }
    }
}