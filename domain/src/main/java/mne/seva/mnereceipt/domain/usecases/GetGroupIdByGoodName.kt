package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetGroupIdByGoodName(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(nameId: Long): Long {
        return receiptRepository.getGroupIdByGoodName(nameId)
    }

}