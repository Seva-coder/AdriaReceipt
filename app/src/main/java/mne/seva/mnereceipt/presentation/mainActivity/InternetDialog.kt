package mne.seva.mnereceipt.presentation.mainActivity

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R

class InternetDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.no_internet_message))
                .setTitle(getString(R.string.no_internet_title))
                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    // User cancelled the dialog
                }

                .setPositiveButton(getString(R.string.scan_anyway_btn_dialog)) {
                        _, _ ->
                    (activity as MainActivity).openQrScanner()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}