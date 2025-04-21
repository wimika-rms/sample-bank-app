package ng.wimika.moneyguardsdkclient.ui.features.login.data.models

import com.google.gson.annotations.SerializedName


data class ClientSessionResponse(
    @SerializedName("SessionId")
    val sessionId: String,
)
