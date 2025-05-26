package ng.wimika.moneyguardsdkclient.ui.features.typing_profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguard_sdk.services.typing_profile.models.TypingProfileResult

class TypingProfileViewModel : ViewModel() {
    private val _result = MutableStateFlow<TypingProfileResult?>(null)
    val result: StateFlow<TypingProfileResult?> = _result.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun matchTypingProfile(text: String, context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val sdk = MoneyGuardSdk.initialize(context)
                val typingProfile = sdk.getTypingProfile()
                val result = typingProfile.matchTypingProfile(text)
                _result.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while matching typing profile"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 