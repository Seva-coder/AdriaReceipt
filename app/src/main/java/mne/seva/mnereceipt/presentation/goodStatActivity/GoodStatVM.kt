package mne.seva.mnereceipt.presentation.goodStatActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.domain.models.NameWithPriceId
import mne.seva.mnereceipt.domain.models.ShopWithPrice
import mne.seva.mnereceipt.domain.usecases.GetGoodCostBetweenDays
import mne.seva.mnereceipt.domain.usecases.GetGroup
import mne.seva.mnereceipt.domain.usecases.GetListShopsPrice
import mne.seva.mnereceipt.domain.usecases.GetOrigNamesById
import java.time.LocalDateTime
import java.time.ZoneOffset


class GoodStatVM(private val getOrigNamesById: GetOrigNamesById,
                 private val getGroup: GetGroup,
                 private val getGoodCostBetweenDays: GetGoodCostBetweenDays,
                 private val getListShopsPrice: GetListShopsPrice
) : ViewModel() {

    private val _listNames = MutableLiveData<List<NameWithPriceId>>()
    val listNames: LiveData<List<NameWithPriceId>> = _listNames

    private val _group = MutableLiveData<String>()
    val group: LiveData<String> = _group

    private val _cost7 = MutableLiveData<Double>()
    val cost7: LiveData<Double> = _cost7

    private val _cost30 = MutableLiveData<Double>()
    val cost30: LiveData<Double> = _cost30

    private val currentTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
    private val currentUnixTimeUtc = currentTime.toEpochSecond(ZoneOffset.UTC)

    private val day7ago: LocalDateTime = currentTime.minusDays(7)
    private val days7agoUnixTimeUtc = day7ago.toEpochSecond(ZoneOffset.UTC)

    private val day30ago: LocalDateTime = currentTime.minusDays(30)
    private val days30agoUnixTimeUtc = day30ago.toEpochSecond(ZoneOffset.UTC)

    private val _priceList = MutableLiveData<List<ShopWithPrice>>()
    val priceList: LiveData<List<ShopWithPrice>> = _priceList

    private var inited = false

    fun setNameId(id: Long) {
        if (!inited) {
            inited = true
            viewModelScope.launch {
                this@GoodStatVM._listNames.postValue(getOrigNamesById.execute(id))
                this@GoodStatVM._group.postValue(getGroup.execute(id))

                this@GoodStatVM._cost7.postValue(
                    getGoodCostBetweenDays.execute(
                        nameId = id,
                        dayFrom = days7agoUnixTimeUtc,
                        dayTo = currentUnixTimeUtc
                    )
                )
                this@GoodStatVM._cost30.postValue(
                    getGoodCostBetweenDays.execute(
                        nameId = id,
                        dayFrom = days30agoUnixTimeUtc,
                        dayTo = currentUnixTimeUtc
                    )
                )

                this@GoodStatVM._priceList.postValue(getListShopsPrice.execute(goodNameId = id))
            }
        }
    }

}