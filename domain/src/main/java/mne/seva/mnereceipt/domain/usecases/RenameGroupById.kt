package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class RenameGroupById(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(id: Long, newName: String) {
        receiptRepository.renameGroupById(id = id, newName = newName)
    }

}