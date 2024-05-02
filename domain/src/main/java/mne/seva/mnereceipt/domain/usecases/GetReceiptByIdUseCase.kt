package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetReceiptByIdUseCase(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(receiptId: Long): DownloadedReceipt {
        return receiptRepository.getReceiptById(receiptId)
    }

}