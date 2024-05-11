package mne.seva.mnereceipt.domain.models

data class ReceiptWithShop(val receiptId: Long, val shopName: String,
                           val date: Long, val total: Double, val byCash: Boolean?,
                           val currencyType: String)