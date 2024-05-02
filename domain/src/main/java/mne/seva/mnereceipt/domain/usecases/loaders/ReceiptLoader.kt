package mne.seva.mnereceipt.domain.usecases.loaders

import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.ReceiptNumber

interface ReceiptLoader {

    suspend fun download(number: ReceiptNumber, defaultName: Boolean,
                         defaultGroup: Boolean, defaultUnit: Boolean): DownloadedReceipt
    fun stopDownload()
    fun linkToNumber(link: String): ReceiptNumber?

}