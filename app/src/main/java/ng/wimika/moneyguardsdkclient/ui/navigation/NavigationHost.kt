package ng.wimika.moneyguardsdkclient.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import ng.wimika.moneyguardsdkclient.ui.LocalToken
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.CheckDebitTransaction
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.CheckDebitTransactionDestination
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.Dashboard
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardDestination
import ng.wimika.moneyguardsdkclient.ui.features.landing.Landing
import ng.wimika.moneyguardsdkclient.ui.features.landing.LandingScreen
import ng.wimika.moneyguardsdkclient.ui.features.login.Login
import ng.wimika.moneyguardsdkclient.ui.features.login.LoginDestination
import ng.wimika.moneyguardsdkclient.ui.features.moneyguard.MoneyGuard
import ng.wimika.moneyguardsdkclient.ui.features.moneyguard.MoneyGuardNavigation
import ng.wimika.moneyguardsdkclient.ui.features.startriskchecks.StartupRiskDestination
import ng.wimika.moneyguardsdkclient.ui.features.startriskchecks.StartupRiskScreen
import ng.wimika.moneyguardsdkclient.ui.features.utility.Utility
import ng.wimika.moneyguardsdkclient.ui.features.utility.UtilityScreen
import ng.wimika.moneyguard_sdk.services.policy.MoneyGuardPolicy
import ng.wimika.moneyguardsdkclient.ui.features.claims.Claim
import ng.wimika.moneyguardsdkclient.ui.features.claims.ClaimDestination
import ng.wimika.moneyguardsdkclient.ui.features.claims.claim_detail.ClaimDetail
import ng.wimika.moneyguardsdkclient.ui.features.claims.claim_detail.ClaimDetailDestination
import ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims.SubmitClaim
import ng.wimika.moneyguardsdkclient.ui.features.claims.submit_claims.SubmitClaimDestination
import ng.wimika.moneyguardsdkclient.ui.features.typing_profile.TypingProfileScreen
import ng.wimika.moneyguardsdkclient.ui.features.typing_profile.VerifyTypingProfileScreen

object Routes {
    const val STARTUP_RISK = "startup_risk"
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard/{token}"
    const val UTILITY = "utility"
    const val MONEY_GUARD = "money_guard"
    const val CHECK_DEBIT = "check_debit"
    const val CLAIM = "claim"
    const val CLAIM_DETAIL = "claim_detail/{claimId}"
    const val SUBMIT_CLAIM = "submit_claim"
    const val TYPING_PROFILE = "typing_profile"
    const val VERIFY_TYPING_PROFILE = "verify_typing_profile/{token}"

    fun getClaimDetailRoute(claimId: Int): String {
        return CLAIM_DETAIL.replace("{claimId}", claimId.toString())
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    isLoggedIn: Boolean = false,
    moneyGuardPolicy: MoneyGuardPolicy,
    token: String
) {
    CompositionLocalProvider(LocalToken provides token) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.STARTUP_RISK
        ) {
            composable(Routes.STARTUP_RISK) {
                StartupRiskDestination(
                    launchLoginScreen = {
                        navController.navigate(Routes.LOGIN)
                    }
                )
            }

            composable(
                route = Routes.LANDING,
                arguments = listOf(
                    androidx.navigation.navArgument("token") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                Log.d("TokenDebug-NavigationHost", "Extracted token from navigation: $token")
                LandingScreen(
                    gotoLoginClick = {
                        navController.navigate(Routes.LOGIN)
                    },
                    token = token
                )
            }

            composable(Routes.LOGIN) {
                LoginDestination(
                    navController = navController,
                    onLoginSuccess = {
                        Log.d("TokenDebug-NavigationHost", "Login success, navigating to dashboard with token: $token")
                        navController.navigate("dashboard/${token}") {
                            popUpTo(Routes.LANDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Routes.DASHBOARD,
                arguments = listOf(
                    androidx.navigation.navArgument("token") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                Log.d("TokenDebug-NavigationHost", "Extracted token for dashboard: $token")
                DashboardDestination(
                    onUtilitiesClick = {
                        navController.navigate(Routes.UTILITY)
                    },
                    onLogout = {
                        navController.navigate(Routes.STARTUP_RISK) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDebitCheckClick = {
                        navController.navigate(Routes.CHECK_DEBIT)
                    },
                    onEnableMoneyGuard = {
                        navController.navigate(Routes.MONEY_GUARD)
                    },
                    onClaimClick = {
                        navController.navigate(Routes.CLAIM)
                    },
                    onTypingProfileClick = {
                        navController.navigate(Routes.TYPING_PROFILE)
                    },
                    token = token
                )
            }

            composable(Routes.UTILITY) {
                UtilityScreen(
                    onNavigateToMoneyGuard = {
                        navController.navigate(Routes.MONEY_GUARD)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.MONEY_GUARD) {
                MoneyGuardNavigation(
                    moneyGuardPolicy = moneyGuardPolicy,
                    token = token,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.CHECK_DEBIT) {
                CheckDebitTransactionDestination(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.CLAIM) {
                ClaimDestination(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    addClaimsClick = {
                        navController.navigate(Routes.SUBMIT_CLAIM)
                    },
                    onClaimItemClick = { id ->
                        navController.navigate(Routes.getClaimDetailRoute(id))
                    }
                )
            }

            composable(
                route = Routes.CLAIM_DETAIL,
                arguments = listOf(
                    androidx.navigation.navArgument("claimId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val claimId = backStackEntry.arguments?.getString("claimId")?.toIntOrNull() ?: 0
                ClaimDetailDestination(
                    claimId = claimId,
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(Routes.SUBMIT_CLAIM) {
                SubmitClaimDestination(
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable(Routes.TYPING_PROFILE) {
                TypingProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.VERIFY_TYPING_PROFILE,
                arguments = listOf(
                    androidx.navigation.navArgument("token") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                VerifyTypingProfileScreen(
                    token = token,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onVerificationSuccess = {
                        navController.navigate("dashboard/${token}") {
                            popUpTo(Routes.LANDING) { inclusive = true }
                        }
                    },
                    onVerificationFailed = {
                        navController.navigate(Routes.LANDING) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}