package mne.seva.mnereceipt.data.storage.entities

data class ReceiptWithShop(val receiptId: Long, val shopName: String,
                           val date: Long, val total: Double, val byCash: Boolean?,
                           val currencyType: String)