package mne.seva.mnereceipt.presentation.costsActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.data.storage.entities.GoodsInGroup
import mne.seva.mnereceipt.domain.usecases.GetCostBetweenDaysUseCase
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class CostsActivityVM(
    val getCostBetweenDaysUseCase: GetCostBetweenDaysUseCase,
    val repository: ReceiptRepositoryImpl
) : ViewModel() {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

    val listGroups = repository.listGroups.asLiveData()

    var groupsInited = false

    private val _checkedGroups = mutableSetOf<Long>()
    val checkedGroups: Set<Long> = _checkedGroups

    private var currentTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
    private var currentUnixTimeUtc = currentTime.toEpochSecond(ZoneOffset.UTC)

    private var day7ago: LocalDateTime = currentTime.minusDays(7)
    private var days7agoUnixTimeUtc = day7ago.toEpochSecond(ZoneOffset.UTC)

    private var day30ago: LocalDateTime = currentTime.minusDays(30)
    private var days30agoUnixTimeUtc = day30ago.toEpochSecond(ZoneOffset.UTC)

    private val _listGoods7days = MutableLiveData<List<GoodsInGroup>>()
    val listGoods7days: LiveData<List<GoodsInGroup>> = _listGoods7days

    private val _listGoods30days = MutableLiveData<List<GoodsInGroup>>()
    val listGoods30days: LiveData<List<GoodsInGroup>> = _listGoods30days

    private fun updGoodsLists() {
        viewModelScope.launch {
            _listGoods7days.postValue(repository.getListGoodsBetweenDays(checkedGroups, days7agoUnixTimeUtc, currentUnixTimeUtc))
            _listGoods30days.postValue(repository.getListGoodsBetweenDays(checkedGroups, days30agoUnixTimeUtc, currentUnixTimeUtc))
        }
    }

    fun loadCheckedGroupsFromBundle(array: LongArray) {
        _checkedGroups.addAll(array.asList())
        calcCosts()
        calcBetween()
        updGoodsLists()
    }

    private val _cost30: MutableLiveData<Double> = MutableLiveData()
    val cost30: LiveData<Double> = _cost30

    private val _cost7: MutableLiveData<Double> = MutableLiveData()
    val cost7: LiveData<Double> = _cost7

    private val _costBetween = MutableLiveData<Pair<Double, Double>>()
    val costBetween: LiveData<Pair<Double, Double>> = _costBetween

    private val _btnFromText = MutableLiveData<String>()
    val btnFromText: LiveData<String> = _btnFromText

    private val _btnToText = MutableLiveData<String>()
    val btnToText: LiveData<String> = _btnToText

    fun upd7and30days() {
        listGroups.value?.forEach {
            _checkedGroups.add(it.id)
        }
        calcCosts()
        calcBetween()
        updGoodsLists()
    }

    fun selectGroupId(id: Long) {
        _checkedGroups.add(id)
        calcCosts()
        calcBetween()
        updGoodsLists()
    }

    fun unSelectGroupTd(id: Long) {
        _checkedGroups.remove(id)
        calcCosts()
        calcBetween()
        updGoodsLists()
    }

    private fun calcCosts() {
        viewModelScope.launch {
            currentTime = LocalDateTime.now(ZoneOffset.UTC)
            currentUnixTimeUtc = currentTime.toEpochSecond(ZoneOffset.UTC)

            this@CostsActivityVM._cost30.postValue(getCostBetweenDaysUseCase.execute(checkedGroups, days30agoUnixTimeUtc, currentUnixTimeUtc))
            this@CostsActivityVM._cost7.postValue(getCostBetweenDaysUseCase.execute(checkedGroups, days7agoUnixTimeUtc, currentUnixTimeUtc))
        }
    }

    private val _listGoodsBetweenDays = MutableLiveData<Pair<List<GoodsInGroup>, Double>>()
    val listGoodsBetweenDays: LiveData<Pair<List<GoodsInGroup>, Double>> = _listGoodsBetweenDays

    private fun calcBetween() {
        viewModelScope.launch {
            if (::dateFrom_.isInitialized && ::dateTo_.isInitialized) {
                val unixDateFrom = dateFrom_.toEpochSecond(ZoneOffset.UTC)
                val unixDateTo = dateTo_.toEpochSecond(ZoneOffset.UTC)

                val days = (unixDateTo - unixDateFrom) / (24 * 60 * 60).toDouble()
                val cost =
                    getCostBetweenDaysUseCase.execute(checkedGroups, unixDateFrom, unixDateTo)
                val costPerDay = cost / days
                _costBetween.postValue(Pair(cost, costPerDay))

                val resList = repository.getListGoodsBetweenDays(checkedGroups, unixDateFrom, unixDateTo)
                _listGoodsBetweenDays.value = Pair(resList, days)
            }
        }
    }

    lateinit var dateFrom_: LocalDateTime
    lateinit var dateTo_: LocalDateTime

    private var _dateNotValid = MutableLiveData<Boolean>()
    val dateNotValid: LiveData<Boolean> = _dateNotValid

    fun dateFromSetted(): Boolean {
        return this::dateFrom_.isInitialized
    }

    fun dateToSetted(): Boolean {
        return this::dateTo_.isInitialized
    }

    fun setDateFrom(date: LocalDateTime) {
        if (this::dateTo_.isInitialized) {
            if (date < dateTo_) {
                this.dateFrom_ = date
                _btnFromText.value = date.format(formatter)
                calcBetween()
            } else {
                _dateNotValid.value = true
            }
        } else {
            this.dateFrom_ = date
            _btnFromText.value = date.format(formatter)
        }
    }

    init {
        setDateTo(date = LocalDateTime.now(ZoneOffset.systemDefault()))
    }

    fun setDateTo(date: LocalDateTime) {
        if (this::dateFrom_.isInitialized) {
            if (dateFrom_ < date) {
                this.dateTo_ = date
                _btnToText.value = date.format(formatter)
                calcBetween()
            } else {
                _dateNotValid.value = true
            }
        } else {
            this.dateTo_ = date
            _btnToText.value = date.format(formatter)
        }
    }

    fun notValidDateShown() {
        _dateNotValid.value = false
    }

}