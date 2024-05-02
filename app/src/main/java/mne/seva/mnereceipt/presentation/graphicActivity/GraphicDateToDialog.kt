package mne.seva.mnereceipt.presentation.graphicActivity

import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.presentation.BaseDatePickerFragment
import mne.seva.mnereceipt.presentation.ReceiptApplication
import java.time.LocalDateTime

class GraphicDateToDialog : BaseDatePickerFragment() {

    override val viewModel: GraphicActivityVM by activityViewModels {
        GraphicActivityVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    override fun setDate(date: LocalDateTime) {
        viewModel.setDateTo(date.plusHours(23).plusMinutes(59).plusSeconds(59))
    }

}