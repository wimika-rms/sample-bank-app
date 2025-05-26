package ng.wimika.moneyguardsdkclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardDestination
import ng.wimika.moneyguardsdkclient.ui.features.typing_profile.TypingProfileScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TypingProfile : Screen("typing_profile")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Dashboard.route) {
            DashboardDestination(
                onUtilitiesClick = { /* TODO */ },
                onEnableMoneyGuard = { /* TODO */ },
                onDebitCheckClick = { /* TODO */ },
                onClaimClick = { /* TODO */ },
                onTypingProfileClick = {
                    navController.navigate(Screen.TypingProfile.route)
                },
                onLogout = { /* TODO */ }
            )
        }

        composable(Screen.TypingProfile.route) {
            TypingProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 