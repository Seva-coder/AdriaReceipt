package mne.seva.mnereceipt.presentation.costsActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.GetCostBetweenDaysUseCase


class CostsVMFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val getNdaysCostUseCase by lazy { GetCostBetweenDaysUseCase(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CostsActivityVM(
            getCostBetweenDaysUseCase = getNdaysCostUseCase,
            repository = repository) as T
    }


}