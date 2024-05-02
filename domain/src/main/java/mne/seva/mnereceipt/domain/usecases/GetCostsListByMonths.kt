package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetCostsListByMonths(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(): List<CostByPeriod> {
        val listFromDB = receiptRepository.getCostsListByMonths()
        if (listFromDB.size <= 1) return listFromDB

        val fixedList = mutableListOf<CostByPeriod>()

        listFromDB.forEachIndexed { index, currentVal ->
            if (index == 0) {
                fixedList.add(currentVal)
            } else {
                val previousIndex = index - 1
                val previousVal = listFromDB[previousIndex]
                if (datesAreSequential(previousVal, currentVal)) {
                    fixedList.add(currentVal)
                } else {
                    fixedList.addAll(genDatesBetween(previousVal, currentVal))
                    fixedList.add(currentVal)
                }
            }
        }
        return fixedList
    }

    private fun datesAreSequential(first: CostByPeriod, second: CostByPeriod): Boolean {
        val year1 = first.text.take(4).toInt()
        val month1 = first.text.takeLast(2).toInt()

        val year2 = second.text.take(4).toInt()
        val month2 = second.text.takeLast(2).toInt()

        return if (year1 == year2) {
            (month2 - month1) == 1
        } else {
            return ((year2 - year1) == 1) && (month1 == 12) && (month2 == 1)
        }
    }

    private fun genDatesBetween(first: CostByPeriod, second: CostByPeriod): List<CostByPeriod> {
        val year1 = first.text.take(4).toInt()
        val month1 = first.text.takeLast(2).toInt()

        val year2 = second.text.take(4).toInt()
        val month2 = second.text.takeLast(2).toInt()

        val list = mutableListOf<CostByPeriod>()

        // case when dates in one year
        if (year1 == year2) {
            for (month in (month1+1) until month2) {
                val strMonth = month.toString().padStart(2, '0')
                list.add(CostByPeriod(text = "$year1-$strMonth", cost = 0.0))
            }
            return list
        }

        // case when different years
        for (month in (month1 + 1)..12) {
            val strMonth = month.toString().padStart(2, '0')
            list.add(CostByPeriod(text = "$year1-$strMonth", cost = 0.0))
        }
        if (year2 - year1 > 1) {
            for (year in (year1+1) until year2) {
                for (month in 1..12) {
                    val strMonth = month.toString().padStart(2, '0')
                    list.add(CostByPeriod(text = "$year-$strMonth", cost = 0.0))
                }
            }
        }
        for (month in 1 until month2) {
            val strMonth = month.toString().padStart(2, '0')
            list.add(CostByPeriod(text = "$year2-$strMonth", cost = 0.0))
        }
        return list
    }

}