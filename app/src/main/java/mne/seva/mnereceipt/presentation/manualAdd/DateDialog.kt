package mne.seva.mnereceipt.presentation.manualAdd


import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.presentation.BaseDatePickerFragment
import mne.seva.mnereceipt.presentation.ReceiptApplication
import java.time.LocalDateTime


class DateDialog : BaseDatePickerFragment() {

    override val viewModel: ManualReceiptAddVM by activityViewModels {
        ManualVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    override fun setDate(date: LocalDateTime) {
        viewModel.setBuyDate(date.plusHours(12))
    }

}