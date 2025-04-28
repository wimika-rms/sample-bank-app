package ng.wimika.moneyguardsdkclient.utils

import java.security.MessageDigest

fun String.computeHash(): String {
    val bytes = this.toByteArray(Charsets.UTF_8)
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}

