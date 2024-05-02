package mne.seva.mnereceipt.presentation.viewReceiptsAct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.data.storage.entities.ReceiptWithShop
import mne.seva.mnereceipt.databinding.ReceiptRowBinding
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ReceiptAdapter(private val viewReceiptsActivity: ViewReceiptsActivity) : ListAdapter<ReceiptWithShop, ReceiptViewHolder>(DiffCallback) {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding: ReceiptRowBinding = ReceiptRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding, viewReceiptsActivity)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val item = getItem(position)
        val date = Instant.ofEpochSecond(item.date).atZone(ZoneOffset.systemDefault())
        val dateStr = date.format(formatter)
        val total = holder.itemView.context.getString(R.string.goodPrice, item.total)
        holder.bind(shopName = item.shopName, dateStr = dateStr, total = total)
    }

    object DiffCallback : DiffUtil.ItemCallback<ReceiptWithShop>() {
        override fun areItemsTheSame(oldItem: ReceiptWithShop, newItem: ReceiptWithShop): Boolean {
            return (oldItem.receiptId == newItem.receiptId)
        }

        override fun areContentsTheSame(oldItem: ReceiptWithShop, newItem: ReceiptWithShop): Boolean {
            return (oldItem == newItem)
        }
    }

}