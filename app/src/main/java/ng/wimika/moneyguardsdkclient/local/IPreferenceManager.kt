package ng.wimika.moneyguardsdkclient.local

interface IPreferenceManager {

    fun saveMoneyGuardToken(token: String?)
    fun getMoneyGuardToken(): String?

    fun saveUserFirstName(firstName: String?)
    fun getUserFirstName(): String?

    fun clear()
}