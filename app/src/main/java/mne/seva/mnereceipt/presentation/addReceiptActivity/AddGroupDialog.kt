package mne.seva.mnereceipt.presentation.addReceiptActivity

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.databinding.AddGroupDialogBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication

class AddGroupDialog : DialogFragment() {

    companion object {
        const val TAG = "AddGroupDialog"
        private const val KEY_POSITION = "POSITION"

        fun newInstance(position: Int): AddGroupDialog {
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            val fragment = AddGroupDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewIdToGroupDbId = mutableMapOf<Int, Long>()
    private val groupDBIdToViewId = mutableMapOf<Long, Int>()

    private var _binding: AddGroupDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = AddGroupDialogBinding.inflate(layoutInflater)
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)

            val viewModel: ReceiptAddViewModel by activityViewModels {
                ReceiptAddVmFactory((requireActivity().application as ReceiptApplication).repository)
            }

            val position = requireArguments().getInt(KEY_POSITION)

            val dialogView: View = binding.root

            binding.newGroup.doAfterTextChanged {
                if (it.toString() != "" && binding.radioGroup.checkedRadioButtonId != -1) {
                    binding.radioGroup.clearCheck()
                }
            }

            this.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.listGroups.collect {
                        binding.radioGroup.removeAllViews()
                        for (gr in it) {
                            val rb = RadioButton(context)
                            val viewId = View.generateViewId()
                            viewIdToGroupDbId[viewId] = gr.id
                            groupDBIdToViewId[gr.id] = viewId
                            rb.id = viewId
                            rb.text = gr.name
                            rb.setOnClickListener {
                                if (binding.newGroup.text.toString() != "") binding.newGroup.setText("")
                            }
                            binding.radioGroup.addView(rb)
                        }

                        if (viewModel.listGoods.value!![position].groupSetted) {
                            val viewRadioId = groupDBIdToViewId[viewModel.listGoods.value!![position].groupId]
                            if (viewRadioId != null) {
                                val preselectedRb = dialogView.findViewById<RadioButton>(viewRadioId)
                                preselectedRb.isChecked = true
                            }
                        }

                    }
                }
            }

            builder.setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    if (binding.newGroup.text.toString() == "" && binding.radioGroup.checkedRadioButtonId != -1) {
                        //radiobutton selected
                        viewModel.setGroupIdForGood(
                            groupId = viewIdToGroupDbId[binding.radioGroup.checkedRadioButtonId] ?: 1L,
                            goodPosition = position
                        )
                    }
                    if (binding.newGroup.text.toString() != "") {
                        //new group name added
                        viewModel.createNewGroupForGood(groupName = binding.newGroup.text.toString(), goodPosition = position)
                    }
                }

                .setNegativeButton("Отмена") { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.newGroup.isCursorVisible = false  // to avoid memory leak android BUG
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}