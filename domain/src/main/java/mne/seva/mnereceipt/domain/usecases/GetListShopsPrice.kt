package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.ShopWithPrice
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetListShopsPrice(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(goodNameId: Long): List<ShopWithPrice> {
        return receiptRepository.getListShopsPrice(goodNameId)
    }

}