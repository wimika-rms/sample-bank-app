package ng.wimika.moneyguardsdkclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardDestination
import ng.wimika.moneyguardsdkclient.ui.features.typing_profile.TypingProfileScreen
import ng.wimika.moneyguardsdkclient.ui.features.typing_profile.VerifyTypingProfileScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object TypingProfile : Screen("typing_profile")
    object VerifyTypingProfile : Screen("verify_typing_profile/{token}") {
        fun createRoute(token: String) = "verify_typing_profile/$token"
    }
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

        composable(
            route = Screen.VerifyTypingProfile.route,
            arguments = listOf(
                navArgument("token") {
                    type = androidx.navigation.NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            VerifyTypingProfileScreen(
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    // Navigate to success screen or dashboard
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onVerificationFailed = {
                    // Navigate back to typing profile screen
                    navController.popBackStack()
                }
            )
        }
    }
} 