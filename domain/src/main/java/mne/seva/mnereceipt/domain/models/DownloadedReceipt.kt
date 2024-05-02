package mne.seva.mnereceipt.domain.models

import java.io.Serializable


class DownloadedReceipt(
    val shopName: String,
    var shopId: Long,
    val dateTimeUnix: Long,
    val totalPrice: Double,
    val byCash: Boolean?,
    val currencyType: String,

    val receiptNumber: ReceiptNumber,

    val listOfGoods: List<NewGood>,
    val country: String
) : Serializable