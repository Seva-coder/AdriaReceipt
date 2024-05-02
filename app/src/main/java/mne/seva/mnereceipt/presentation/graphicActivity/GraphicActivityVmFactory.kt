package mne.seva.mnereceipt.presentation.graphicActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.GetCostsListByDates
import mne.seva.mnereceipt.domain.usecases.GetCostsListByMonths
import mne.seva.mnereceipt.domain.usecases.GetCostsListByWeeks
import mne.seva.mnereceipt.domain.usecases.GetGroupCostListFromTo


class GraphicActivityVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val getGroupCostListFromTo by lazy { GetGroupCostListFromTo(receiptRepository = repository) }
    private val getCostsListByDates by lazy { GetCostsListByDates(receiptRepository = repository) }
    private val getCostsListByWeeks by lazy { GetCostsListByWeeks(receiptRepository = repository) }
    private val getCostsListByMonths by lazy { GetCostsListByMonths(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GraphicActivityVM(
            getGroupCostListFromTo = getGroupCostListFromTo,
            getCostsListByDates = getCostsListByDates,
            getCostsListByWeeks = getCostsListByWeeks,
            getCostsListByMonths = getCostsListByMonths) as T
    }

}