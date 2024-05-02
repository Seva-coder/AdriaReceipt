package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class DeleteNameIfEmpty(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(nameId: Long) {
        if (receiptRepository.countNameUses(nameId = nameId) == 0) {
            receiptRepository.deleteNameById(nameId = nameId)
        }
    }

}