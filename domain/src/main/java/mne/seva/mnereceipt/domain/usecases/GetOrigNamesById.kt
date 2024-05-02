package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.NameWithPriceId
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetOrigNamesById(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(id: Long): List<NameWithPriceId> {
        return receiptRepository.getOrigNamesById(id)
    }

}