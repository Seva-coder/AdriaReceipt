package mne.seva.mnereceipt.presentation.viewReceiptsAct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.usecases.GetReceiptByIdUseCase

class ViewReceiptVM(private val getReceiptByIdUseCase: GetReceiptByIdUseCase,
                    repository: ReceiptRepositoryImpl) : ViewModel() {

    val listReceipt = repository.listReceipts

    lateinit var receiptToOpen: DownloadedReceipt

    private var _showReceipt = MutableLiveData<Boolean>()
    val showReceipt: LiveData<Boolean> = _showReceipt

    fun openReceipt(receiptId: Long) {
        viewModelScope.launch {
            receiptToOpen = getReceiptByIdUseCase.execute(receiptId = receiptId)
            _showReceipt.value = true
        }
    }

    fun receiptShown() {
        _showReceipt.value = false
    }

}