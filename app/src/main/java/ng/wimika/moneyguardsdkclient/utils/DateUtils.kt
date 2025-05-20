package ng.wimika.moneyguardsdkclient.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat

object DateUtils {
    private val formatters = listOf(
        ISODateTimeFormat.dateTime(),
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    )

    fun formatDate(dateTime: String): String {
        var parsedDateTime: DateTime? = null
        var lastError: Exception? = null

        for (formatter in formatters) {
            try {
                parsedDateTime = formatter.parseDateTime(dateTime)
                break
            } catch (e: Exception) {
                lastError = e
                continue
            }
        }

        if (parsedDateTime == null) {
            throw lastError ?: IllegalArgumentException("Could not parse date: $dateTime")
        }

        val outputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        return outputFormatter.print(parsedDateTime)
    }

    fun formatDateTime(dateTime: DateTime): String {
        val outputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        return outputFormatter.print(dateTime)
    }
}