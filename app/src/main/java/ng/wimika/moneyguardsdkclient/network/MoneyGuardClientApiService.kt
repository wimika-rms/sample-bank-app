package ng.wimika.moneyguardsdkclient.network

import ng.wimika.moneyguardsdkclient.ui.features.login.data.models.ClientLoginRequest
import ng.wimika.moneyguardsdkclient.ui.features.login.data.models.ClientSessionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MoneyGuardClientApiService {

    @POST("api/v1/account/auth/emails/signin")
    suspend fun login(
        @Body loginRequest: ClientLoginRequest
    ): ClientSessionResponse
}