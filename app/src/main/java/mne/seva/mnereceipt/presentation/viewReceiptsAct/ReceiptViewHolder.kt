package mne.seva.mnereceipt.presentation.viewReceiptsAct

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.databinding.ReceiptRowBinding

class ReceiptViewHolder(val binding: ReceiptRowBinding, private val viewReceiptsActivity: ViewReceiptsActivity) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(shopName: String, dateStr: String, total: String) {
        binding.shopName.text = shopName
        binding.date.text = dateStr
        binding.total.text = total
    }

    override fun onClick(p0: View?) {
        viewReceiptsActivity.openReceipt(position = adapterPosition)
    }

    interface CallBackFromAdapter {
        fun openReceipt(position: Int)
    }
}