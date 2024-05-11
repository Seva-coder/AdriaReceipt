package mne.seva.mnereceipt.domain.usecases

class ArrayToCsvString {

    fun execute(inputArray: Array<Array<String>>): String {

        val result = StringBuilder()
        inputArray.forEach {
            val str = it.joinToString(separator = ";")
            result.appendLine(str)
        }

        return result.toString()
    }

}