package ng.wimika.moneyguardsdkclient.utils

import java.text.NumberFormat
import java.util.*

object CurrencyFormatter {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("en", "NG")).apply {
        currency = Currency.getInstance("NGN")
        maximumFractionDigits = 0
    }

    fun format(amount: Int): String {
        return formatter.format(amount.toLong())
    }

    fun format(amount: Long): String {
        return formatter.format(amount)
    }
} 