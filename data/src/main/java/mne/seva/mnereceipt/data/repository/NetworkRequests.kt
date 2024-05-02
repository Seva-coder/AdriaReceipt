package mne.seva.mnereceipt.data.repository


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import okhttp3.FormBody.Builder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import java.util.concurrent.TimeUnit


class NetworkRequests {

    private var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    suspend fun downloadReceiptJSON(number: ReceiptNumber): String {

        val formBody: RequestBody = Builder()
            .add("iic", number.iic)
            .add("tin", number.tin)
            .add("dateTimeCreated", number.date_time)
            .build()
        val request = Request.Builder()
            .url("https://mapr.tax.gov.me/ic/api/verifyInvoice")
            .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:109.0) Gecko/20100101 Firefox/113.0")
            .addHeader("Referer", "https://mapr.tax.gov.me/ic/")
            .addHeader("Origin", "https://mapr.tax.gov.me")
            .post(formBody)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("${response.code} ${response.message}")
                }
                response.body!!.string()
            }
        }
    }

    fun stopAllRequests() {
        client.dispatcher.cancelAll()
    }

}