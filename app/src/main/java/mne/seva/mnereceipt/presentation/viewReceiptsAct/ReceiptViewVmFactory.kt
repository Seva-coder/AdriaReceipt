package mne.seva.mnereceipt.presentation.viewReceiptsAct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.GetReceiptByIdUseCase


class ReceiptViewVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val getReceiptByIdUseCase by lazy { GetReceiptByIdUseCase(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ViewReceiptVM(getReceiptByIdUseCase = getReceiptByIdUseCase,
                            repository = repository) as T
    }

}