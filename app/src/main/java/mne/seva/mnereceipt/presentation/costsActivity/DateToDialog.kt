package mne.seva.mnereceipt.presentation.costsActivity

import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.presentation.BaseDatePickerFragment
import mne.seva.mnereceipt.presentation.ReceiptApplication
import java.time.LocalDateTime

open class DateToDialog : BaseDatePickerFragment() {

    override val viewModel: CostsActivityVM by activityViewModels {
        CostsVMFactory((requireActivity().application as ReceiptApplication).repository)
    }

    override fun setDate(date: LocalDateTime) {
        viewModel.setDateTo(date.plusHours(23).plusMinutes(59).plusSeconds(59))
    }

}