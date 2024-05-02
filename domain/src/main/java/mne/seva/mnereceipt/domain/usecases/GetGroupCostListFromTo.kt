package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.GroupCost
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetGroupCostListFromTo(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(dayFrom: Long, dayTo: Long): List<GroupCost> {
        return receiptRepository.getGroupCostListFromTo(dayFrom, dayTo)
    }

}