package ng.wimika.moneyguardsdkclient.network

import ng.wimika.moneyguardsdkclient.ui.features.login.data.models.ClientSessionResponse
import retrofit2.http.POST

interface MoneyGuardClientApiService {

    @POST("/auth/emails/signin")
    suspend fun login(email: String, password: String): ClientSessionResponse
}