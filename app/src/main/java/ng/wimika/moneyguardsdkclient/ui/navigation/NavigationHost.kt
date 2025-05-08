package ng.wimika.moneyguardsdkclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.CheckDebitTransaction
import ng.wimika.moneyguardsdkclient.ui.features.checkdebit.CheckDebitTransactionDestination
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.Dashboard
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardDestination
import ng.wimika.moneyguardsdkclient.ui.features.dashboard.DashboardScreen
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

@Composable
fun NavigationHost(
    navController: NavHostController,
    isLoggedIn: Boolean = false,
    moneyGuardPolicy: MoneyGuardPolicy,
    token: String
) {
    NavHost(
        navController = navController,
        //startDestination = if (isLoggedIn) StartupRiskScreen else Landing
        startDestination = if (isLoggedIn) Dashboard else Landing
        // Comment out prelaunch checks for now
        // startDestination = if (isLoggedIn) StartupRiskScreen else Landing
        startDestination = if (isLoggedIn) Dashboard else Landing
    ) {
        // Comment out prelaunch checks for now
        /*
        composable<StartupRiskScreen> {
            StartupRiskDestination(
                launchMainScreen =  {
                    if (isLoggedIn) {
                        navController.navigate(Dashboard) {
                            popUpTo(Landing) { inclusive = true }
                        }
                        return@StartupRiskDestination
                    }

                    navController.navigate(Landing)
                }
            )
        }


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
                   // navController.navigate(StartupRiskScreen) {
                    navController.navigate(Dashboard) {
                    // Navigate directly to dashboard after login
                    navController.navigate(Dashboard) {
                        popUpTo(Landing) { inclusive = true }
                    }
                }
            )
        }

        composable<Dashboard> {
            DashboardDestination(
                onUtilitiesClick = {
                    navController.navigate(Utility)
                },
                onLogout = {
                    navController.navigate(Landing) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                },
                onDebitCheckClick = {
                    navController.navigate(CheckDebitTransaction)
                },
                onEnableMoneyGuard = {
                    navController.navigate(MoneyGuard)
                }
            )
        }

        composable<Utility> {
            UtilityScreen()
        }

        composable<MoneyGuard> {
            MoneyGuardNavigation(
                moneyGuardPolicy = moneyGuardPolicy,
                token = token,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable <CheckDebitTransaction>{
            CheckDebitTransactionDestination()
        }
    }
}