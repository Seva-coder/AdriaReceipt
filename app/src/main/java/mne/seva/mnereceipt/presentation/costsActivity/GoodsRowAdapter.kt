package mne.seva.mnereceipt.presentation.costsActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.data.storage.entities.GoodsInGroup
import mne.seva.mnereceipt.databinding.CostsTableRowBinding


class GoodsRowAdapter : ListAdapter<GoodsInGroup, GoodsViewHolder>(DiffCallback) {

    var days = 1.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val binding: CostsTableRowBinding = CostsTableRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoodsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        val item = getItem(position)
        val name = item.name

        val qntStr = holder.itemView.context.getString(R.string.quantityInReceipt, item.quantity, item.suffix)
        val qntPerDayStr = holder.itemView.context.getString(R.string.quantity_per_day, item.quantity / days, item.suffix)
        val costPerDay = holder.itemView.context.getString(R.string.euro_per_day, item.total / days)
        val totalStr = holder.itemView.context.getString(R.string.goodPrice, item.total)
        holder.bind(name = name, quantityStr = qntStr, quantityPerDay = qntPerDayStr, costPerDay = costPerDay,
            totalStr = totalStr)
    }


    object DiffCallback : DiffUtil.ItemCallback<GoodsInGroup>() {
        override fun areItemsTheSame(oldItem: GoodsInGroup, newItem: GoodsInGroup): Boolean {
            return (oldItem.nameId == newItem.nameId)
        }

        override fun areContentsTheSame(oldItem: GoodsInGroup, newItem: GoodsInGroup): Boolean {
            return (oldItem == newItem)
        }

    }
}