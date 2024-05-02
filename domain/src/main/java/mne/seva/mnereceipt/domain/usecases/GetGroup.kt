package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetGroup(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(id: Long): String {
        return receiptRepository.getGroup(id)
    }

}