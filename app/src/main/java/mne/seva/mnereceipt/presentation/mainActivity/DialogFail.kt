package mne.seva.mnereceipt.presentation.mainActivity

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R

class DialogFail : DialogFragment()  {

    companion object {
        const val TAG = "DialogFail"
        private const val KEY_TITLE = "TITLE"
        private const val KEY_MESSAGE = "MESSAGE"

        fun newInstance(title: String, message: String): DialogFail {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_MESSAGE, message)

            val fragment = DialogFail()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val title = requireArguments().getString(KEY_TITLE)
            val message = requireArguments().getString(KEY_MESSAGE)
            builder.setMessage(message)
                .setTitle(title)
                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    // User cancelled the dialog
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}