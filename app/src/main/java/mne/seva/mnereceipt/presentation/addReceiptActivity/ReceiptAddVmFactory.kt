package mne.seva.mnereceipt.presentation.addReceiptActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.CreateNewGroupUseCase
import mne.seva.mnereceipt.domain.usecases.WriteReceiptToDbUseCase


class ReceiptAddVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val createNewGroupUseCase by lazy { CreateNewGroupUseCase(receiptRepository = repository) }
    private val writeReceiptToDbUseCase by lazy { WriteReceiptToDbUseCase(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ReceiptAddViewModel(createNewGroupUseCase = createNewGroupUseCase,
                                    writeReceiptToDbUseCase = writeReceiptToDbUseCase,
                                    repository = repository) as T
    }

}