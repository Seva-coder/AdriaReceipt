package mne.seva.mnereceipt.presentation.costsActivity

import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.databinding.CostsTableRowBinding

class GoodsViewHolder(val binding: CostsTableRowBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(name: String, quantityStr: String, quantityPerDay: String, costPerDay: String, totalStr: String) {
        binding.newName.text = name
        binding.quantity.text = quantityStr
        binding.quantityPerDay.text = quantityPerDay
        binding.costPerDay.text = costPerDay
        binding.total.text = totalStr
    }

}