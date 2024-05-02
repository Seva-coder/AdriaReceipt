package mne.seva.mnereceipt.presentation.manualAdd

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.SelectSuffixDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class UnitDialog : DialogFragment() {

    private var _binding: SelectSuffixDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "UnitDialog"
        private const val KEY_POSITION = "POSITION"

        fun newInstance(position: Int): UnitDialog {
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            val fragment = UnitDialog()
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
        _binding = SelectSuffixDialogBinding.inflate(layoutInflater)
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val dialogView: View = binding.root

            val position = requireArguments().getInt(KEY_POSITION)

            val vm: ManualReceiptAddVM by activityViewModels {
                ManualVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            if (vm.listGoods.value!![position].suffixSetted) {
                @Suppress("MoveVariableDeclarationIntoWhen")
                val suffix = vm.listGoods.value!![position].suffix
                when(suffix) {
                    "kg" -> binding.kg.isChecked = true
                    "L" -> binding.L.isChecked = true
                    "pc" -> binding.piece.isChecked = true
                }
            }

            builder.setView(dialogView)
                .setPositiveButton(getString(R.string.ok_dialog_btn)
                ) { _, _ ->
                    if (binding.radioGroup.checkedRadioButtonId != -1) {
                        when(binding.radioGroup.checkedRadioButtonId) {
                            R.id.kg -> vm.setSuffixForGood(suffix = "kg", goodPosition = position)
                            R.id.L -> vm.setSuffixForGood(suffix = "L", goodPosition = position)
                            R.id.piece -> vm.setSuffixForGood(suffix = "pc", goodPosition = position)
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.empty_unit_fail_toast), Toast.LENGTH_LONG).show()
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