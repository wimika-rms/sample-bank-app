package ng.wimika.moneyguardsdkclient.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat


object DateUtils {

    fun formatDate(dateTime: String): String {

        val dateTime = try {
            val isoFormatter = ISODateTimeFormat.dateTime()
            isoFormatter.parseDateTime(dateTime)
        }catch (error: IllegalArgumentException) {
            error.printStackTrace()
            val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
            formatter.parseDateTime(dateTime)
        }

        val outputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        return outputFormatter.print(dateTime)
    }
}