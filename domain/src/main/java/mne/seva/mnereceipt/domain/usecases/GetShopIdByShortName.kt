package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetShopIdByShortName(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(name: String): Long? {
        return receiptRepository.getShopIdByShortName(name)
    }

}