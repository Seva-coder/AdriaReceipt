package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.exceptions.ReceiptAlreadyExistException
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.repository.ReceiptRepository


class IsReceiptExistUseCase(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(number: ReceiptNumber) {
        val exist = receiptRepository.isReceiptExist(number)
        if (exist) {
            throw ReceiptAlreadyExistException()
        }
    }

}