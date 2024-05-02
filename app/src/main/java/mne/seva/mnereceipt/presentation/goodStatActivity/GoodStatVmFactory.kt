package mne.seva.mnereceipt.presentation.goodStatActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.GetGoodCostBetweenDays
import mne.seva.mnereceipt.domain.usecases.GetGroup
import mne.seva.mnereceipt.domain.usecases.GetListShopsPrice
import mne.seva.mnereceipt.domain.usecases.GetOrigNamesById


class GoodStatVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val getOrigNamesById by lazy { GetOrigNamesById(receiptRepository = repository) }
    private val getGroup by lazy { GetGroup(receiptRepository = repository) }
    private val getGoodCostBetweenDays by lazy { GetGoodCostBetweenDays(receiptRepository = repository) }
    private val getListShopsPrice by lazy { GetListShopsPrice(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GoodStatVM(getOrigNamesById = getOrigNamesById,
                            getGroup = getGroup,
                            getGoodCostBetweenDays = getGoodCostBetweenDays,
                            getListShopsPrice = getListShopsPrice) as T
    }

}