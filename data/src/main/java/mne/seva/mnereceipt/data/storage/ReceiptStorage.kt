package mne.seva.mnereceipt.data.storage

import mne.seva.mnereceipt.domain.models.DownloadedReceipt

interface ReceiptStorage {

    suspend fun insertReceipt(time: Long, downloadedReceipt: DownloadedReceipt): Long

}