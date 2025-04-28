package ng.wimika.moneyguardsdkclient.local

interface IPreferenceManager {

    fun saveMoneyGuardToken(token: String?)
    fun getMoneyGuardToken(): String?

    fun clear()
}