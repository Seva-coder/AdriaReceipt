package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class UpdateGood(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(goodId: Long, groupId: Long, nameId: Long, suffix: String) {
        receiptRepository.updateGood(goodId = goodId, groupId = groupId, nameId = nameId, suffix = suffix)
    }

}