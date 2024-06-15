package mne.seva.mnereceipt.domain.usecases

import mne.seva.mnereceipt.domain.repository.ReceiptRepository
import java.util.Locale

class ExportGroupSpends(private val receiptRepository: ReceiptRepository) {

    private val emptyAnswer = Array(1) { Array(1) { "NO DATA"} }

    suspend fun execute(): Array<Array<String>> {
        val minDate = receiptRepository.getMinReceiptTime()
        val maxDate = receiptRepository.getMaxReceiptTime()

        if (minDate == null || maxDate == null) return emptyAnswer

        val groupList = receiptRepository.getAllGroupList()

        val groupIds = groupList.map { it.id }
        val groupNames = groupList.map { it.name }

        val spendsHashmapsList = mutableListOf<Map<String, Double>>()

        groupIds.forEach { id ->
            val groupSpends = receiptRepository.exportGroupSpends(id)
            val currentHashMap = groupSpends.associate { it.time to it.spent }
            spendsHashmapsList.add(currentHashMap)
        }

        val answer: MutableList<MutableList<String>> = mutableListOf()
        val header = mutableListOf<String>()
        header.add("")  // first column - dates
        header.addAll(groupNames)
        header.add("sum")  // last column - sum of row
        answer.add(header)

        val datesList = genDatesList(startDate = minDate, endDate = maxDate)

        datesList.forEach { date ->
            val row = mutableListOf<String>()
            row.add(date)
            var sum = 0.0
            spendsHashmapsList.forEach { hashMap ->
                val spent = hashMap.getOrDefault(date, 0.0)
                sum += spent
                row.add(String.format(locale = Locale.US,"%.2f", spent))
            }
            row.add(String.format(locale = Locale.US,"%.2f", sum))
            answer.add(row)
        }

        val rows = answer.size
        val cols = answer[0].size
        val answerArr = Array(rows){ Array(cols){""} }

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                answerArr[i][j] = answer[i][j]
            }
        }

        return answerArr
    }


    private fun genDatesList(startDate: String, endDate: String): Array<String> {
        val startYear = startDate.split("-")[0].toInt()
        val startMonth = startDate.split("-")[1].toInt()

        val answerList = mutableListOf<String>()
        answerList.add(startDate)

        var currYear = startYear
        var currMonth = startMonth

        if (startDate == endDate) return Array(1) { startDate }

        var strMonth = currMonth.toString().padStart(2, '0')

        while ("$currYear-$strMonth" != endDate) {

            if (currMonth < 12) {
                currMonth++
            }
            else {
                currYear++
                currMonth = 1
            }
            strMonth = currMonth.toString().padStart(2, '0')
            answerList.add("$currYear-$strMonth")
        }

        return answerList.toTypedArray()
    }

}