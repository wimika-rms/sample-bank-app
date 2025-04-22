package ng.wimika.moneyguardsdkclient.ui.features.login.data.models

import com.google.gson.annotations.SerializedName
import ng.wimika.moneyguardsdkclient.network.BaseResponse

data class ClientSession(
    @SerializedName("sessionId")
    val sessionId: String,
)


class ClientSessionResponse: BaseResponse<ClientSession>()
