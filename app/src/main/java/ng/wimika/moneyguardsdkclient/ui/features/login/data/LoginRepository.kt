package ng.wimika.moneyguardsdkclient.ui.features.login.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ng.wimika.moneyguardsdkclient.network.MoneyGuardClientApiService
import ng.wimika.moneyguardsdkclient.network.NetworkUtils
import ng.wimika.moneyguardsdkclient.ui.features.login.data.models.ClientSessionResponse

interface LoginRepository {
    suspend fun login(email: String, password: String): Flow<String>
}


class LoginRepositoryImpl: LoginRepository {

    private val apiService: MoneyGuardClientApiService by lazy {
        NetworkUtils.getRetrofitClient("https://bankservice.azurewebsites.net/")
            .create(MoneyGuardClientApiService::class.java)
    }

    override suspend fun login(email: String, password: String): Flow<String> = flow {
        val response = apiService.login(email, password)
        emit(response.sessionId)
    }

}