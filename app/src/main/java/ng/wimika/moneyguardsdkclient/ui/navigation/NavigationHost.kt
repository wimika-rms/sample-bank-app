package ng.wimika.moneyguardsdkclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
            startDestination = if (isLoggedIn) Dashboard else StartupRiskScreen
        ) {

            composable<StartupRiskScreen> {
                StartupRiskDestination(
                    launchLoginScreen = {
                        navController.navigate(Login)
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
                    },
                    onClaimClick = {
                        navController.navigate(Claim)
                    })
            }

            composable<Utility> {
                UtilityScreen(
                    onNavigateToMoneyGuard = {
                        navController.navigate(MoneyGuard)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
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

            composable<CheckDebitTransaction> {
                CheckDebitTransactionDestination(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }


            composable<Claim> {
                ClaimDestination(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    addClaimsClick = {
                        navController.navigate(SubmitClaim)
                    },
                    onClaimItemClick = { id ->
                        navController.navigate(ClaimDetail(claimId = id))
                    }
                )
            }

            composable<ClaimDetail> { backStackEntry ->
                val claimDetail = backStackEntry.toRoute<ClaimDetail>()
                ClaimDetailDestination(
                    claimId = claimDetail.claimId,
                    onBackPressed = { navController.popBackStack() }
                )
            }

            composable<SubmitClaim> {
                SubmitClaimDestination(
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
    }
}