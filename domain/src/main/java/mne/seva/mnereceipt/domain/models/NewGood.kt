package mne.seva.mnereceipt.domain.models

import java.io.Serializable


data class NewGood(
    var goodId: Long,

    var nameOrig: String, var newNameSetted: Boolean,
    var newName: String, var newNameId: Long,

    var quantity: Double,

    var suffix: String, var suffixSetted: Boolean,

    var price: Double,
    val currencyType: String,

    var total: Double,
    val code: String?,

    var groupId: Long,
    var groupSetted: Boolean,
    val country: String
) : Serializable