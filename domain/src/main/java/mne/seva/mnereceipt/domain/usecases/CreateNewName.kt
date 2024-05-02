package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class CreateNewName(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(newName: String): Long {
        val newNameId = receiptRepository.getNewNameId(newName)
        return if (newNameId == -1L) {
            receiptRepository.insertNewName(newName)
        } else {
            newNameId
        }
    }

}