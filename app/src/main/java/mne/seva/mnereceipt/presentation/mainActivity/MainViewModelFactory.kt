package mne.seva.mnereceipt.presentation.mainActivity


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.IsReceiptExistUseCase
import mne.seva.mnereceipt.domain.usecases.loaders.LoadMNEReceipt
import mne.seva.mnereceipt.domain.usecases.ReceiptProviderByLink

class MainViewModelFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val isReceiptExistUseCase by lazy { IsReceiptExistUseCase(receiptRepository = repository) }
    private val loadMNEReceipt by lazy { LoadMNEReceipt(receiptRepository = repository) }


    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(isReceiptExistUseCase = isReceiptExistUseCase,
                            loadMNEReceipt = loadMNEReceipt,
                            receiptProviderByLink = ReceiptProviderByLink()
        ) as T
    }
}
