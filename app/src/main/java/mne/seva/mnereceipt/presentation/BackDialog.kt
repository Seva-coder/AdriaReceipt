package mne.seva.mnereceipt.presentation

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R

class BackDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.exit_back_dialog_message))
                .setTitle(getString(R.string.exit_back_dialog_title))
                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    // User cancelled the dialog
                }

                .setPositiveButton(getString(R.string.exit_back_dialog_ok_btn)) {
                        _, _ ->
                    requireActivity().finish()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}