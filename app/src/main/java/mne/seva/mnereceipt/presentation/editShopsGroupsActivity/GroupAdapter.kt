package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import mne.seva.mnereceipt.databinding.GroupRowBinding
import mne.seva.mnereceipt.domain.models.Group


class GroupAdapter(private val editShopsActivity: EditShopsGroupsActivity) :
    ListAdapter<Group, GroupViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding: GroupRowBinding = GroupRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding, editShopsActivity)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val item = getItem(position)
        val groupName = item.name
        holder.bind(groupName = groupName)
    }

    object DiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return (oldItem == newItem)
        }
    }

}