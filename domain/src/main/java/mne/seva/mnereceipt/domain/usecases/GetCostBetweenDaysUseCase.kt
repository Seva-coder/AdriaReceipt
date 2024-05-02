package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetCostBetweenDaysUseCase(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(set: Set<Long>, dayFrom: Long, dayTo: Long): Double {
        return receiptRepository.getCostBetweenDays(set, dayFrom, dayTo)
    }

}