package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetGoodCostBetweenDays(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(nameId: Long, dayFrom: Long, dayTo: Long): Double {
        return receiptRepository.getGoodCostBetweenDays(nameId, dayFrom, dayTo)
    }

}