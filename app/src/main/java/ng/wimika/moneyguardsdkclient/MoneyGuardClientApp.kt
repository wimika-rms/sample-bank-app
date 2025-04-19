package ng.wimika.moneyguardsdkclient

import android.app.Application
import ng.wimika.moneyguard_sdk.MoneyGuardSdk
import ng.wimika.moneyguard_sdk.services.MoneyGuardSdkService

class MoneyGuardClientApp: Application() {

    companion object {
        var sdkService: MoneyGuardSdkService? = null
    }

    override fun onCreate() {
        super.onCreate()
        sdkService = MoneyGuardSdk.initialize(this)
    }
}