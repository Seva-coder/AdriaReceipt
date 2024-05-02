package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.AddShopDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class EditGroupDialog : DialogFragment() {

    companion object {
        const val TAG = "EditGroupDialog"
        private const val KEY_OLD_NAME = "name"
        private const val KEY_ID = "id"

        fun newInstance(id: Long, oldName: String): EditGroupDialog {
            val args = Bundle()
            args.putLong(KEY_ID, id)
            args.putString(KEY_OLD_NAME, oldName)
            val fragment = EditGroupDialog()
            fragment.arguments = args
            return fragment
        }
    }

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

            val vm: EditShopsVM by requireActivity().viewModels {
                EditShopsVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            val idDB = requireArguments().getLong(KEY_ID)
            val oldName = requireArguments().getString(KEY_OLD_NAME)

            val dialogView: View = binding.root

            builder.setView(dialogView)
            builder.setTitle(getString(R.string.rename_shop_dialog_title, oldName))
                .setPositiveButton(getString(R.string.rename_dialog_btn)) { _, _ ->
                    val name = binding.shopName.text.toString()
                    if (name != "") {
                        vm.renameGroup(id = idDB, newName = name)
                    } else {
                        Toast.makeText(requireContext().applicationContext, getString(R.string.empty_name_fail_toast), Toast.LENGTH_SHORT).show()
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
        binding.shopName.isCursorVisible = false  // to avoid memory leak android BUG
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}