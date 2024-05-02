package mne.seva.mnereceipt.presentation.addReceiptActivity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.AddShopDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class AddShopDialog : DialogFragment() {

    private var _binding: AddShopDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddShopDialogBinding.inflate(layoutInflater)
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val vm: ReceiptAddViewModel by requireActivity().viewModels {
                ReceiptAddVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            val dialogView: View = binding.root

            if (vm.shopName.value != "") {
                binding.shopName.setText(vm.shopName.value)
            }

            builder.setView(dialogView)
            builder.setTitle(getString(R.string.new_shop_name_title))
                .setPositiveButton(getString(R.string.ok_dialog_btn)) { _, _ ->
                    vm.setShopName(binding.shopName.text.toString())
                }

                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.shopName.isCursorVisible = false  // to avoid memory leak android BUG
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}