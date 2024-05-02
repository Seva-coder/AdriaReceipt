package mne.seva.mnereceipt.domain.usecases

enum class ReceiptProvider {
    MNE,
    SRB,
    NOT_EXIST
}

class ReceiptProviderByLink {

    fun execute(link: String): ReceiptProvider {
        return if (link.startsWith(prefix = "https://mapr.tax.gov.me", ignoreCase = true) ||
            link.startsWith(prefix = "http://mapr.tax.gov.me", ignoreCase = true)) {
            ReceiptProvider.MNE
        } else {
            ReceiptProvider.NOT_EXIST
        }
    }

}