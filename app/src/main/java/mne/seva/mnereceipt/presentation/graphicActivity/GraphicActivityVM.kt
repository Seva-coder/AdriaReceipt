package mne.seva.mnereceipt.presentation.graphicActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.models.GroupCost
import mne.seva.mnereceipt.domain.usecases.GetCostsListByDates
import mne.seva.mnereceipt.domain.usecases.GetCostsListByMonths
import mne.seva.mnereceipt.domain.usecases.GetCostsListByWeeks
import mne.seva.mnereceipt.domain.usecases.GetGroupCostListFromTo
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class GraphicActivityVM(val getGroupCostListFromTo: GetGroupCostListFromTo,
                        val getCostsListByDates: GetCostsListByDates,
                        val getCostsListByWeeks: GetCostsListByWeeks,
                        val getCostsListByMonths: GetCostsListByMonths
): ViewModel() {

    private val _listGroupCosts7days = MutableLiveData<List<GroupCost>>()
    val listGroupCosts7days: LiveData<List<GroupCost>> = _listGroupCosts7days

    private val _listGroupCosts30days = MutableLiveData<List<GroupCost>>()
    val listGroupCosts30days: LiveData<List<GroupCost>> = _listGroupCosts30days

    private val _listGroupCostsCustom = MutableLiveData<List<GroupCost>>()
    val listGroupCostsCustom: LiveData<List<GroupCost>> = _listGroupCostsCustom

    private val _listCostsByDates = MutableLiveData<List<CostByPeriod>>()
    val listCostsByDates: LiveData<List<CostByPeriod>> = _listCostsByDates

    private val _listCostsByWeeks = MutableLiveData<List<CostByPeriod>>()
    val listCostsByWeeks: LiveData<List<CostByPeriod>> = _listCostsByWeeks

    private val _listCostsByMonths = MutableLiveData<List<CostByPeriod>>()
    val listCostsByMonths: LiveData<List<CostByPeriod>> = _listCostsByMonths

    private val _btnFromText = MutableLiveData<String>()
    val btnFromText: LiveData<String> = _btnFromText

    private val _btnToText = MutableLiveData<String>()
    val btnToText: LiveData<String> = _btnToText

    private val _inputDateError = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val inputDateError = _inputDateError.asSharedFlow()

    private lateinit var dateFrom_: LocalDateTime
    private lateinit var dateTo_: LocalDateTime
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

    init {
        val currentTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        val currentUnixTimeUtc = currentTime.toEpochSecond(ZoneOffset.UTC)

        val day7ago: LocalDateTime = currentTime.minusDays(7)
        val days7agoUnixTimeUtc = day7ago.toEpochSecond(ZoneOffset.UTC)

        val day30ago: LocalDateTime = currentTime.minusDays(30)
        val days30agoUnixTimeUtc = day30ago.toEpochSecond(ZoneOffset.UTC)


        viewModelScope.launch {
            this@GraphicActivityVM._listGroupCosts7days.postValue(getGroupCostListFromTo.execute(dayFrom = days7agoUnixTimeUtc, dayTo = currentUnixTimeUtc))
            this@GraphicActivityVM._listGroupCosts30days.postValue(getGroupCostListFromTo.execute(dayFrom = days30agoUnixTimeUtc, dayTo = currentUnixTimeUtc))

            this@GraphicActivityVM._listCostsByDates.postValue(getCostsListByDates.execute())
            this@GraphicActivityVM._listCostsByWeeks.postValue(getCostsListByWeeks.execute())
            this@GraphicActivityVM._listCostsByMonths.postValue(getCostsListByMonths.execute())
        }
    }

    fun setDateTo(date: LocalDateTime) {
        if (this::dateFrom_.isInitialized) {
            if (dateFrom_ < date) {
                this.dateTo_ = date
                _btnToText.value = date.format(formatter)
                calcBetween()
            } else {
                _inputDateError.tryEmit(true)
            }
        } else {
            this.dateTo_ = date
            _btnToText.value = date.format(formatter)
        }
    }

    fun setDateFrom(date: LocalDateTime) {
        if (this::dateTo_.isInitialized) {
            if (date < dateTo_) {
                this.dateFrom_ = date
                _btnFromText.value = date.format(formatter)
                calcBetween()
            } else {
                _inputDateError.tryEmit(true)
            }
        } else {
            this.dateFrom_ = date
            _btnFromText.value = date.format(formatter)
        }
    }

    private fun calcBetween() {
        viewModelScope.launch {
            if (::dateFrom_.isInitialized && ::dateTo_.isInitialized) {
                val unixDateFrom = dateFrom_.toEpochSecond(ZoneOffset.UTC)
                val unixDateTo = dateTo_.toEpochSecond(ZoneOffset.UTC)

                val groupsCost = getGroupCostListFromTo.execute(dayFrom = unixDateFrom, dayTo = unixDateTo)
                animNdaysCompleted = false
                _listGroupCostsCustom.postValue(groupsCost)
            }
        }
    }

    private var animCompleted = false

    fun animDone() {
        animCompleted = true
    }

    fun isAnimCompleted(): Boolean {
        return animCompleted
    }

    private var animNdaysCompleted = false

    fun animNdaysDone() {
        animNdaysCompleted = true
    }

    fun isAnimNdaysCompleted(): Boolean {
        return animNdaysCompleted
    }

}