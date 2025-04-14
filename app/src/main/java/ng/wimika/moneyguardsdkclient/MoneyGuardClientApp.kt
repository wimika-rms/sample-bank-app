package ng.wimika.moneyguardsdkclient

import android.app.Application
import ng.wimika.moneyguard_sdk.MoneyGuardSdk

class MoneyGuardClientApp: Application() {

    override fun onCreate() {
        super.onCreate()
        MoneyGuardSdk.initialize(this)
    }
}