package mne.seva.mnereceipt.domain.models

import java.io.Serializable

class ReceiptNumber(val iic: String, val tin: String, val date_time: String) : Serializable
