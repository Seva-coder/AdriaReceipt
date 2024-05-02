package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import mne.seva.mnereceipt.databinding.ShopRowBinding
import mne.seva.mnereceipt.domain.models.Shop


class ShopAdapter(private val editShopsActivity: EditShopsGroupsActivity) : ListAdapter<Shop, ShopViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ShopRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding, editShopsActivity)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val item = getItem(position)
        val nameOrig = item.origName
        val nameNew = item.newName
        holder.bind(shopNameOrig = nameOrig, shopNameNew = nameNew)
    }

    object DiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean {
            return (oldItem == newItem)
        }
    }
}