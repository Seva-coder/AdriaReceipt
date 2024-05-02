package mne.seva.mnereceipt.presentation.goodsActivity

import androidx.lifecycle.ViewModel
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl

class GoodsActivityVM(val repository: ReceiptRepositoryImpl) : ViewModel() {

    val listAllGoods = repository.listAllGoods


}