package mne.seva.mnereceipt.data.storage.entities

data class GoodWithQuantity(
    val name: String,
    val price: Double,
    val suffix: String,
    val quantity: Double
)