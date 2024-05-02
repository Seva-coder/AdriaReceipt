package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.Group
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetAllGroups(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(): List<Group> {
        return receiptRepository.getAllGroups()
    }

}