package ng.wimika.moneyguardsdkclient.ui.features.moneyguard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ng.wimika.moneyguard_sdk.services.moneyguard_policy.models.BankAccount
import ng.wimika.moneyguard_sdk.services.moneyguard_policy.models.PolicyOption
import ng.wimika.moneyguard_sdk.services.policy.MoneyGuardPolicy

sealed class MoneyGuardScreen(val route: String) {
    object AccountSelection : MoneyGuardScreen("account_selection")
    object CoverageLimitSelection : MoneyGuardScreen("coverage_limit_selection")
    object PolicyOptionSelection : MoneyGuardScreen("policy_option_selection")
    object Summary : MoneyGuardScreen("summary")
}

@Composable
fun MoneyGuardNavigation(
    navController: NavHostController = rememberNavController(),
    moneyGuardPolicy: MoneyGuardPolicy,
    token: String,
    onBack: () -> Unit
) {
    var selectedAccounts: List<BankAccount> by remember { mutableStateOf(emptyList()) }
    var selectedPolicyOption: PolicyOption? by remember { mutableStateOf(null) }

    NavHost(
        navController = navController,
        startDestination = "account_selection"
    ) {
        composable("account_selection") {
            AccountSelectionScreen(
                moneyGuardPolicy = moneyGuardPolicy,
                token = token,
                onBack = onBack,
                onContinue = { accounts ->
                    selectedAccounts = accounts
                    navController.navigate("coverage_limit_selection")
                }
            )
        }

        composable("coverage_limit_selection") {
            CoverageLimitSelectionScreen(
                moneyGuardPolicy = moneyGuardPolicy,
                token = token,
                onBack = { navController.popBackStack() },
                onContinue = { coverageLimitId ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "coverageLimitId",
                        coverageLimitId
                    )
                    navController.navigate("policy_option_selection")
                }
            )
        }

        composable("policy_option_selection") {
            val coverageLimitId = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Int>("coverageLimitId")
                ?: return@composable

            PolicyOptionSelectionScreen(
                moneyGuardPolicy = moneyGuardPolicy,
                token = token,
                coverageLimitId = coverageLimitId,
                onBack = { navController.popBackStack() },
                onContinue = { policyOption ->
                    selectedPolicyOption = policyOption
                    navController.navigate("summary")
                }
            )
        }

        composable("summary") {
            if (selectedAccounts.isEmpty() || selectedPolicyOption == null) {
                navController.popBackStack()
                return@composable
            }

            SummaryScreen(
                selectedAccounts = selectedAccounts,
                selectedPolicyOption = selectedPolicyOption!!,
                onBack = { navController.popBackStack() },
                onCheckout = {
                    // Handle checkout
                }
            )
        }
    }
} 