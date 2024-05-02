package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class WriteReceiptToDbUseCase(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(newShopName: String, receipt: DownloadedReceipt) {
        receiptRepository.saveReceipt(newShopName = newShopName, downloadedReceipt = receipt)
    }

}