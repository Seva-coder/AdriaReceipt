package mne.seva.mnereceipt.presentation.goodsActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl


class GoodsActivityVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GoodsActivityVM(repository = repository) as T
    }

}