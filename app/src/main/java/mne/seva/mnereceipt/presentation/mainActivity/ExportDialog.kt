package mne.seva.mnereceipt.presentation.mainActivity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ExportDialogBinding


class ExportDialog : DialogFragment() {

    private var _binding: ExportDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ExportDialogBinding.inflate(layoutInflater)
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val dialogView: View = binding.root

            builder.setView(dialogView)
                .setTitle(getString(R.string.export_toolbar_title))
                .setPositiveButton(getString(R.string.ok_dialog_btn)) { _, _ ->
                    if (binding.radioGroup.checkedRadioButtonId != -1) {
                        when(binding.radioGroup.checkedRadioButtonId) {
                            R.id.export_receipts -> (activity as? MainActivity)?.exportReceiptsCsv()
                            //R.id.export_by_month -> (activity as? MainActivity)?.exportReceiptsCsv()
                        }
                    }
                }

                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}