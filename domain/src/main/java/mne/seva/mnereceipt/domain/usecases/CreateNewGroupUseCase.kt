package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class CreateNewGroupUseCase(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(groupName: String): Long {
        val groupId = receiptRepository.getGroupId(groupName)
        return if (groupId == -1L) {
            receiptRepository.insertGroup(groupName)
        } else {
            groupId
        }
    }

}