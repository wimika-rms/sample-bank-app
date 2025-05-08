package ng.wimika.moneyguardsdkclient.ui.features.moneyguard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ng.wimika.moneyguard_sdk.services.moneyguard_policy.models.BankAccount
import ng.wimika.moneyguard_sdk.services.moneyguard_policy.models.PolicyOption

class MoneyGuardViewModel : ViewModel() {
    private val _selectedAccounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val selectedAccounts: List<BankAccount>
        get() = _selectedAccounts.value

    private val _selectedCoverageLimitId = MutableStateFlow<Int?>(null)
    val selectedCoverageLimitId: Int
        get() = _selectedCoverageLimitId.value ?: throw IllegalStateException("Coverage limit not selected")

    private val _selectedPolicyOption = MutableStateFlow<PolicyOption?>(null)
    val selectedPolicyOption: PolicyOption
        get() = _selectedPolicyOption.value ?: throw IllegalStateException("Policy option not selected")

    fun setSelectedAccounts(accounts: List<BankAccount>) {
        _selectedAccounts.value = accounts
    }

    fun setSelectedCoverageLimitId(coverageLimitId: Int) {
        _selectedCoverageLimitId.value = coverageLimitId
    }

    fun setSelectedPolicyOption(policyOption: PolicyOption) {
        _selectedPolicyOption.value = policyOption
    }
} 