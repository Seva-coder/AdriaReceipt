package mne.seva.mnereceipt.presentation.manualAdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.CreateNewGroupUseCase
import mne.seva.mnereceipt.domain.usecases.GetShopIdByShortName
import mne.seva.mnereceipt.domain.usecases.WriteReceiptToDbUseCase

class ManualVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val createNewGroupUseCase by lazy { CreateNewGroupUseCase(receiptRepository = repository) }
    private val writeReceiptToDbUseCase by lazy { WriteReceiptToDbUseCase(receiptRepository = repository) }
    private val getShopIdByShortName by lazy { GetShopIdByShortName(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ManualReceiptAddVM(
            createNewGroupUseCase = createNewGroupUseCase,
            writeReceiptToDbUseCase = writeReceiptToDbUseCase,
            getShopIdByShortName = getShopIdByShortName,
            repository = repository
        ) as T
    }

}