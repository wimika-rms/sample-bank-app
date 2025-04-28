package ng.wimika.moneyguardsdkclient.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager(private val context: Context): IPreferenceManager {

    companion object {
        private const val MONEY_GUARD_TOKEN = "moneyguard_token"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("moneyguard.client.preference", Context.MODE_PRIVATE)
    }

    override fun saveMoneyGuardToken(token: String?) {
        sharedPreferences.edit { putString(MONEY_GUARD_TOKEN, token) }
    }

    override fun getMoneyGuardToken(): String? {
        return sharedPreferences.getString(MONEY_GUARD_TOKEN, null)
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

}