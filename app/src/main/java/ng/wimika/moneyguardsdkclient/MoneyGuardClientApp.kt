package ng.wimika.moneyguardsdkclient

import android.app.Application
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguard_sdk.services.MoneyGuardSdkService
import ng.wimika.moneyguardsdkclient.local.IPreferenceManager
import ng.wimika.moneyguardsdkclient.local.PreferenceManager

class MoneyGuardClientApp: Application() {

    companion object {
        var sdkService: MoneyGuardSdkService? = null
        var preferenceManager: IPreferenceManager? = null
    }

    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(this)
        sdkService = MoneyGuardSdk.initialize(this)
    }
}