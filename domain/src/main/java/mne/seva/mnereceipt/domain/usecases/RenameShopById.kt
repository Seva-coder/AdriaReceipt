package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class RenameShopById(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(shopId: Long, newName: String): Boolean {
        return if (!receiptRepository.isShopExist(newName)) {
            receiptRepository.updateShop(shopId = shopId, newName = newName)
            true
        } else {
            false
        }
    }

}