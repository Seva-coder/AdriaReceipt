package mne.seva.mnereceipt.presentation.editGoodActivity

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


class EditGoodDialog : DialogFragment() {

    companion object {
        const val TAG = "EditGoodNameDialog"
        private const val EDIT_NAME = "POSITION"

        fun newInstance(editName: Boolean): EditGoodDialog {
            val args = Bundle()
            args.putBoolean(EDIT_NAME, editName)
            val fragment = EditGoodDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: AddNameDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddNameDialogBinding.inflate(layoutInflater)
        return activity?.let { myActivity ->
            val builder = AlertDialog.Builder(myActivity)

            val dialogView: View = binding.root

            val vm: EditGoodVM by activityViewModels {
                EditGoodVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            val editName = requireArguments().getBoolean(EDIT_NAME)
            if (editName) {
                // if dialog adding name - add dropdown menu for autocomplete
                this.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                        vm.listNewNames.collect {
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
            }

            val title = if (editName) getString(R.string.new_name_dialog_title) else getString(R.string.dialog_title_new_group)

            builder.setView(dialogView)
                .setTitle(title)
                .setPositiveButton(getString(R.string.ok_dialog_btn)) { _, _ ->
                    if (binding.newName.text.toString() != "") {
                        if (editName) {
                            vm.setNewName(binding.newName.text.toString())
                        } else {
                            vm.setNewGroup(binding.newName.text.toString())
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.empty_name_fail_toast), Toast.LENGTH_LONG).show()
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