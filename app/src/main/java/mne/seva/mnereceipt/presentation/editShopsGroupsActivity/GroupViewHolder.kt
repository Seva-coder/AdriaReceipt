package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.databinding.GroupRowBinding

class GroupViewHolder(val binding: GroupRowBinding, private val editShopsActivity: EditShopsGroupsActivity) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(groupName: String) {
        binding.groupName.text = groupName
    }

    override fun onClick(p0: View?) {
        editShopsActivity.showEditGroupDialog(position = adapterPosition)
    }

    interface EditGroupCallback {
        fun showEditGroupDialog(position: Int)
    }
}