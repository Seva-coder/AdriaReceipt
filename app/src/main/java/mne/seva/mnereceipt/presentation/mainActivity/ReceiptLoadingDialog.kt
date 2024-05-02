package mne.seva.mnereceipt.presentation.mainActivity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ReceiptLoadingDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication

class ReceiptLoadingDialog : DialogFragment() {

    companion object {
        const val TAG = "loadingDialogTag"
    }

    private var _binding: ReceiptLoadingDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ReceiptLoadingDialogBinding.inflate(layoutInflater)
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val dialogView: View = binding.root

            val vm: MainViewModel by requireActivity().viewModels {
                MainViewModelFactory((requireActivity().application as ReceiptApplication).repository)
            }

            builder.setView(dialogView)
                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    vm.stopDownloading()
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