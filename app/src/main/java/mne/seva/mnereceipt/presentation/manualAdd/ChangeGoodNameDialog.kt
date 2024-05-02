package mne.seva.mnereceipt.presentation.manualAdd

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.AddNameDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class ChangeGoodNameDialog : DialogFragment() {

    private var _binding: AddNameDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ChangeGoodNameDialog"
        private const val KEY_POSITION = "POSITION"

        fun newInstance(position: Int): ChangeGoodNameDialog {
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            val fragment = ChangeGoodNameDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddNameDialogBinding.inflate(layoutInflater)
        return activity?.let { myActivity ->
            val builder = AlertDialog.Builder(myActivity)

            val dialogView: View = binding.root

            val vm: ManualReceiptAddVM by activityViewModels {
                ManualVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            this.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    vm.listNames.collect {
                        val adapter = context?.let { cont ->
                            ArrayAdapter(
                                cont,
                                android.R.layout.simple_dropdown_item_1line, it
                            )
                        }
                        binding.newName.setAdapter(adapter)
                    }
                }
            }

            val position = requireArguments().getInt(KEY_POSITION)

            if (vm.listGoods.value!![position].newNameSetted) {
                binding.newName.setText(vm.listGoods.value!![position].newName)
                binding.newName.dismissDropDown()
                binding.newName.requestFocus()
            }

            builder.setView(dialogView)
                .setTitle(getString(R.string.rename_good_dialog_title, vm.listGoods.value!![position].nameOrig))
                .setPositiveButton(getString(R.string.ok_dialog_btn)
                ) { _, _ ->
                    if (binding.newName.text.toString() != "") {
                        vm.setNewNameForGood(
                            newName = binding.newName.text.toString(),
                            goodPosition = position
                        )
                    } else {
                        Toast.makeText(activity, getString(R.string.empty_name_fail_toast), Toast
                            .LENGTH_LONG)
                            .show()
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
        binding.newName.isCursorVisible = false  // to avoid memory leak android BUG
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}