package mne.seva.mnereceipt.domain.usecases.loaders

import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.repository.ReceiptRepository


class LoadMNEReceipt(private val receiptRepository: ReceiptRepository) : ReceiptLoader {

    override suspend fun download(number: ReceiptNumber, defaultName: Boolean,
                                  defaultGroup: Boolean, defaultUnit: Boolean): DownloadedReceipt {
        return receiptRepository.downloadMNEreceipt(number = number, defaultName = defaultName,
            defaultGroup = defaultGroup, defaultUnit = defaultUnit)
    }

    override fun stopDownload() {
        receiptRepository.stopDownload()
    }

    override fun linkToNumber(link: String): ReceiptNumber? {
        val regex_iic = "(?<=iic=)([0-9A-Fa-f]+)".toRegex()
        val match_iic = regex_iic.find(link)
        val iic: String = match_iic?.value ?: ""

        val regex_tin = "(?<=tin=)([0-9]+)".toRegex()
        val match_tin = regex_tin.find(link)
        val tin: String = match_tin?.value ?: ""

        val regex_date_time = "(?<=crtd=)(\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d[+-Z](\\d\\d:\\d\\d)?)".toRegex()
        val match_date_time = regex_date_time.find(link)
        val date_time: String = match_date_time?.value ?: ""

        return if (iic == "" || tin == "" || date_time == "") {
            null
        } else {
            ReceiptNumber(iic = iic, tin = tin, date_time = date_time)
        }
    }

}