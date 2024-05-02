package mne.seva.mnereceipt.presentation.goodsActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import mne.seva.mnereceipt.databinding.GoodRowBinding
import mne.seva.mnereceipt.domain.models.NameWithId


class GoodsAdapter(private val goodsActivity: GoodsActivity) : ListAdapter<NameWithId, GoodsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val binding = GoodRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsViewHolder(binding, goodsActivity)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(id = item.id, name = item.name)
    }

    object DiffCallback : DiffUtil.ItemCallback<NameWithId>() {
        override fun areItemsTheSame(oldItem: NameWithId, newItem: NameWithId): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: NameWithId, newItem: NameWithId): Boolean {
            return (oldItem == newItem)
        }
    }

}