package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class ExportReceiptsCsv(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(): Array<Array<String>> {
        val allReceipts = receiptRepository.exportAllReceipts()
        val result = Array(allReceipts.size + 1) { Array(6) {""} }

        result[0][0] = "№"
        result[0][1] = "Shop"
        result[0][2] = "Date"
        result[0][3] = "Total"
        result[0][4] = "Currency"
        result[0][5] = "ByCash"

        allReceipts.forEachIndexed { index, receiptWithShop ->
            val instant = Instant.ofEpochMilli(receiptWithShop.date * 1000L)
            val time = LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault())
            val timeStr = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            result[index+1][0] = (index+1).toString()
            result[index+1][1] = receiptWithShop.shopName
            result[index+1][2] = timeStr
            result[index+1][3] = String.format(locale = Locale.US,"%.2f", receiptWithShop.total)
            result[index+1][4] = receiptWithShop.currencyType

            if (receiptWithShop.byCash == null) {
                result[index+1][5] = "?"
            } else {
                result[index+1][5] = if (receiptWithShop.byCash) "+" else "-"
            }

        }

        return result
    }

}