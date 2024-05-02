package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class UpdateName(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(nameId: Long, newName: String) {
        receiptRepository.updateName(nameId = nameId, newName = newName)
    }

}