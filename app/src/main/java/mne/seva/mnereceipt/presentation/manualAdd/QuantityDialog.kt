package mne.seva.mnereceipt.presentation.manualAdd

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.AddPriceDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication

class QuantityDialog : DialogFragment() {

    private var _binding: AddPriceDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ChangeQuantityDialog"
        private const val KEY_POSITION = "POSITION"

        fun newInstance(position: Int): QuantityDialog {
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            val fragment = QuantityDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddPriceDialogBinding.inflate(layoutInflater)
        return activity?.let { activ ->
            val builder = AlertDialog.Builder(activ)

            val dialogView: View = binding.root

            val vm: ManualReceiptAddVM by activityViewModels {
                ManualVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            val position = requireArguments().getInt(KEY_POSITION)

            if (vm.listGoods.value!![position].quantity != 0.0) {
                binding.editTextNumberDecimal.setText(vm.listGoods.value!![position].quantity.toString())
                binding.editTextNumberDecimal.requestFocus()
            }

            builder.setView(dialogView)
                .setTitle(getString(R.string.quantity_dialog_title, vm.listGoods.value!![position].newName))
                .setPositiveButton(getString(R.string.ok_dialog_btn)
                ) { _, _ ->
                    if (binding.editTextNumberDecimal.text.toString() != "") {
                        vm.setQuantityForGood(
                            newQuantity = binding.editTextNumberDecimal.text.toString().toDouble(),
                            goodPosition = position
                        )
                    } else {
                        Toast.makeText(activity, getString(R.string.quantity_empty_fail_toast), Toast.LENGTH_LONG).show()
                    }
                }

                .setNegativeButton(getString(R.string.cancel_dialog_btn)) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.editTextNumberDecimal.isCursorVisible = false  // to avoid memory leak android BUG
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}