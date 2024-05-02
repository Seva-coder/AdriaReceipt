package mne.seva.mnereceipt.presentation.manualAdd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.usecases.CreateNewGroupUseCase
import mne.seva.mnereceipt.domain.usecases.GetShopIdByShortName
import mne.seva.mnereceipt.domain.usecases.WriteReceiptToDbUseCase
import java.time.LocalDateTime
import java.time.ZoneOffset

class ManualReceiptAddVM(private val createNewGroupUseCase: CreateNewGroupUseCase,
                         private val writeReceiptToDbUseCase: WriteReceiptToDbUseCase,
                         private val getShopIdByShortName: GetShopIdByShortName,
                         val repository: ReceiptRepositoryImpl) : ViewModel() {

    val listShops = repository.listShops
    val listNames = repository.listNames
    val listGroups = repository.listGroups

    private val _shopName = MutableLiveData("")
    val shopName: LiveData<String> = _shopName

    private val _date = MutableLiveData<LocalDateTime>()
    val date: LiveData<LocalDateTime> = _date

    private val _listGoods = MutableLiveData<ArrayList<NewGood>>()
    val listGoods: LiveData<ArrayList<NewGood>> = _listGoods

    private val _addReceiptButton = MutableLiveData(false)
    val addReceiptButton: LiveData<Boolean> = _addReceiptButton

    private val _shopBtnOk = MutableLiveData(false)
    val shopBtnOk: LiveData<Boolean> = _shopBtnOk

    private val _dateBtnOk = MutableLiveData(false)
    val dateBtnOk: LiveData<Boolean> = _dateBtnOk

    private val _closeActivity = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val closeActivity = _closeActivity.asSharedFlow()

    init {
        // make list in liveData not null
        _listGoods.value = ArrayList(0)
        addNewGood()
    }

    fun setShopName(name: String) {
        _shopName.value = name
        _shopBtnOk.value = true
        checkAllGoods()
    }

    fun setBuyDate(date: LocalDateTime) {
        _date.value = date
        _dateBtnOk.value = true
        checkAllGoods()
    }

    fun restoreGoodsList(list: ArrayList<NewGood>) {
        _listGoods.value = list
    }

    fun addNewGood() {
        val good = NewGood(goodId = 0L, nameOrig = "", newNameSetted = false,
            newName = "", newNameId = 0L, quantity = 0.0,
            suffix = "", suffixSetted = false,
            price = 0.0, currencyType = "EUR",
            total = 0.0, code = null,
            groupId = 0, groupSetted = false,
        country = "MNE")  // TODO: country depends from settings
        _listGoods.value?.add(good)
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun deleteGood(position: Int) {
        if (_listGoods.value?.size == 1) return
        if (position <= (_listGoods.value?.lastIndex ?: 0)) {
            _listGoods.value?.removeAt(position)
            _listGoods.value = _listGoods.value
        }
    }

    fun setNewNameForGood(newName: String, goodPosition: Int) {
        _listGoods.value!![goodPosition].newName = newName
        _listGoods.value!![goodPosition].nameOrig = newName
        _listGoods.value!![goodPosition].newNameSetted = true
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun setGroupIdForGood(groupId: Long, goodPosition: Int) {
        _listGoods.value!![goodPosition].groupSetted = true
        _listGoods.value!![goodPosition].groupId = groupId
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun createNewGroupForGood(groupName: String, goodPosition: Int) {
        viewModelScope.launch {
            val groupId = createNewGroupUseCase.execute(groupName)
            setGroupIdForGood(groupId, goodPosition)
            checkAllGoods()
        }
    }

    fun setNewPriceForGood(newPrice: Double, goodPosition: Int) {
        _listGoods.value!![goodPosition].price = newPrice
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun setQuantityForGood(newQuantity: Double, goodPosition: Int) {
        _listGoods.value!![goodPosition].quantity = newQuantity
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun setSuffixForGood(suffix: String, goodPosition: Int) {
        _listGoods.value!![goodPosition].suffix = suffix
        _listGoods.value!![goodPosition].suffixSetted = true
        _listGoods.value = _listGoods.value
        checkAllGoods()
    }

    fun checkAllGoods() {
        _addReceiptButton.value = _listGoods.value!!.all {
            (it.groupSetted && it.newNameSetted && it.suffixSetted && it.price != 0.0 && it.quantity != 0.0)
        } && (_shopName.value != "") && (_date.value != null)
    }

    fun addReceipt(byCash: Boolean) {
        viewModelScope.launch {
            val shopName = shopName.value ?: ""

            val shopId = getShopIdByShortName.execute(shopName) ?: 0L

            val downloadedReceipt = DownloadedReceipt(
                shopName = shopName,
                shopId = shopId,
                dateTimeUnix = date.value!!.toEpochSecond(ZoneOffset.UTC),
                totalPrice = listGoods.value!!.sumOf { it.price * it.quantity },
                byCash = byCash,
                currencyType = "EUR",
                listOfGoods = listGoods.value!!,
                receiptNumber = ReceiptNumber(iic = "", tin = "", date_time = ""),
                country = "MNE"  // TODO: depend from country setting
            )
            writeReceiptToDbUseCase.execute(newShopName = shopName, receipt = downloadedReceipt)
            _closeActivity.emit(true)
        }
    }

}