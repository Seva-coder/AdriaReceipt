package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.repository.ReceiptRepository
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class GetCostsListByDates(private val receiptRepository: ReceiptRepository) {

    suspend fun execute(): List<CostByPeriod> {
        val listFromDB = receiptRepository.getCostsListByDates()
        if (listFromDB.size <= 1) return listFromDB

        // adding days when was not any shop, cost=0
        // and converting long date string to short
        val fixedList = mutableListOf<CostByPeriod>()
        listFromDB.forEachIndexed { index, currentVal ->
            if (index == 0) {
                fixedList.add(CostByPeriod(text = shortDate(currentVal.text), cost = currentVal.cost))
            } else {
                val previousIndex = index - 1
                val previousVal = listFromDB[previousIndex]
                if (datesAreSequential(previousVal, currentVal)) {
                    fixedList.add(CostByPeriod(text = shortDate(currentVal.text), cost = currentVal.cost))
                } else {
                    fixedList.addAll(genDatesBetween(previousVal, currentVal))
                    fixedList.add(CostByPeriod(text = shortDate(currentVal.text), cost = currentVal.cost))
                }
            }
        }
        return fixedList
    }

    private fun shortDate(date: String): String {
        val parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
        return parsedDate.format(DateTimeFormatter.ofPattern("d MMM"))
    }

    private fun datesAreSequential(first: CostByPeriod, second: CostByPeriod): Boolean {
        val date1 = LocalDate.parse(first.text, DateTimeFormatter.ISO_LOCAL_DATE)
        val date2 = LocalDate.parse(second.text, DateTimeFormatter.ISO_LOCAL_DATE)
        val delta = Period.between(date1, date2)
        val days = delta.days
        return (days <= 1)
    }

    private fun genDatesBetween(first: CostByPeriod, second: CostByPeriod): List<CostByPeriod> {
        val date1 = LocalDate.parse(first.text, DateTimeFormatter.ISO_LOCAL_DATE)
        val date2 = LocalDate.parse(second.text, DateTimeFormatter.ISO_LOCAL_DATE)

        val list = mutableListOf<CostByPeriod>()

        if (date1 >= date2) {
            return list
        }

        var dateToAdd = date1.plusDays(1L)

        do {
            list.add(CostByPeriod(text = dateToAdd.format(DateTimeFormatter.ofPattern("d MMM")), cost = 0.0))
            dateToAdd = dateToAdd.plusDays(1L)
        } while (!dateToAdd.equals(date2))
        return list
    }

}