package mne.seva.mnereceipt.presentation.addReceiptActivity


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.domain.usecases.CreateNewGroupUseCase
import mne.seva.mnereceipt.domain.usecases.WriteReceiptToDbUseCase
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ReceiptAddViewModel(private val createNewGroupUseCase: CreateNewGroupUseCase,
                          private val writeReceiptToDbUseCase: WriteReceiptToDbUseCase,
                          repository: ReceiptRepositoryImpl
// google allow direct rep. access https://developer.android.com/topic/architecture/domain-layer
// make this to not import "kotlinx" to domain
) : ViewModel() {

    private var _shopName = MutableLiveData<String>()
    val shopName: LiveData<String> = _shopName

    private var _dateTime = MutableLiveData<String>()
    val dateTime: LiveData<String> = _dateTime

    private var _total = MutableLiveData<Double>()
    val total: LiveData<Double> = _total

    private var _byCash = MutableLiveData<Boolean?>()
    val byCash: LiveData<Boolean?> = _byCash

    private var _showButtonShop = MutableLiveData<Boolean>()
    val showButtonShop: LiveData<Boolean> = _showButtonShop

    private var _listGoods = MutableLiveData<List<NewGood>>()
    val listGoods: LiveData<List<NewGood>> = _listGoods

    private var _addReceiptButton = MutableLiveData<Boolean>()
    val addReceiptButton: LiveData<Boolean> = _addReceiptButton

    private var _showShopNameFail = MutableLiveData<Boolean>()
    val showShopNameFail: LiveData<Boolean> = _showShopNameFail

    private var _closeActivity = MutableLiveData<Boolean>()
    val closeActivity: LiveData<Boolean> = _closeActivity

    val listGroups = repository.listGroups
    val listNames = repository.listNames

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM HH:mm")

    private lateinit var downloadedReceipt: DownloadedReceipt
    private var receiptLoaded = false
    fun setReceipt(receipt: DownloadedReceipt, showOnly: Boolean) {
        if (!receiptLoaded || showOnly) {
            _shopName.value = receipt.shopName
            _dateTime.value = Instant.ofEpochSecond(receipt.dateTimeUnix)
                .atZone(ZoneOffset.systemDefault()).format(formatter)
            _total.value = receipt.totalPrice
            _byCash.value = receipt.byCash

            _showButtonShop.value = (receipt.shopId == 0L)

            _addReceiptButton.value = false

            _listGoods.value = receipt.listOfGoods.sortedBy { it.goodId }

            receiptLoaded = true
            downloadedReceipt = receipt
        }
    }

    fun setShopName(name: String) {
        if (name == "") {
            _showShopNameFail.value = true
        } else {
            _shopName.value = name
        }
    }

    fun failNameShown() {
        _showShopNameFail.value = false
    }

    fun setGroupIdForGood(groupId: Long, goodPosition: Int) {
        _listGoods.value!![goodPosition].groupSetted = true
        _listGoods.value!![goodPosition].groupId = groupId
        _listGoods.value = _listGoods.value
    }

    fun createNewGroupForGood(groupName: String, goodPosition: Int) {
        viewModelScope.launch {
            val groupId = createNewGroupUseCase.execute(groupName)
            setGroupIdForGood(groupId, goodPosition)
        }
    }

    fun setNewNameForGood(newName: String, goodPosition: Int) {
        _listGoods.value!![goodPosition].newName = newName
        _listGoods.value!![goodPosition].newNameSetted = true
        _listGoods.value = _listGoods.value
    }

    fun setSuffixForGood(suffix: String, goodPosition: Int) {
        _listGoods.value!![goodPosition].suffix = suffix
        _listGoods.value!![goodPosition].suffixSetted = true
        _listGoods.value = _listGoods.value
    }

    fun checkAllGoods() {
        if (_listGoods.value!!.all { (it.groupSetted && it.newNameSetted && it.suffixSetted) || it.goodId != 0L } ) {
            _addReceiptButton.value = true
        }
    }

    fun addReceipt() {
        _addReceiptButton.value = false
        viewModelScope.launch {
            writeReceiptToDbUseCase.execute(newShopName = shopName.value ?: "", receipt = downloadedReceipt)
            _closeActivity.value = true
        }
    }

    fun activityClosed() {
        _closeActivity.value = false
    }

}