package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.repository.ReceiptRepository

class GetCostsListByWeeks(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(): List<CostByPeriod> {
        val listFromDB = receiptRepository.getCostsListByWeeks()
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
        val week1 = first.text.takeLast(2).toInt()

        val year2 = second.text.take(4).toInt()
        val week2 = second.text.takeLast(2).toInt()

        return if (year1 == year2) {
            (week2 - week1) == 1
        } else {
            true  // don't fix when years are different
        }

    }

    private fun genDatesBetween(first: CostByPeriod, second: CostByPeriod): List<CostByPeriod> {
        val year1 = first.text.take(4).toInt()
        val week1 = first.text.takeLast(2).toInt()

        val week2 = second.text.takeLast(2).toInt()

        val list = mutableListOf<CostByPeriod>()

        for (week in (week1 + 1) until week2) {
            list.add(CostByPeriod(text = "$year1-$week", cost = 0.0))
        }
        return list
    }

}