package mne.seva.mnereceipt.presentation.graphicActivity

import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import mne.seva.mnereceipt.domain.models.CostByPeriod


class BarAdapter : ListAdapter<CostByPeriod, BarViewHolder>(DiffCallback) {

    var maxCost = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarViewHolder {
        val view = BarView(parent.context)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        view.setPadding(5)
        return BarViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarViewHolder, position: Int) {
        val item = getItem(position)
        val cost = item.cost
        val text = item.text
        val costRelative = if (maxCost != 0.0) cost / maxCost else 0.0
        holder.bind(cost = costRelative, text = text)
    }

    override fun onCurrentListChanged(previousList: MutableList<CostByPeriod>,
                                      currentList: MutableList<CostByPeriod>) {
        super.onCurrentListChanged(previousList, currentList)
        maxCost = currentList.maxByOrNull { it.cost }?.cost ?: 0.0
    }

    object DiffCallback : DiffUtil.ItemCallback<CostByPeriod>() {
        override fun areItemsTheSame(oldItem: CostByPeriod, newItem: CostByPeriod): Boolean {
            return (oldItem.text == newItem.text)
        }

        override fun areContentsTheSame(oldItem: CostByPeriod, newItem: CostByPeriod): Boolean {
            return (oldItem == newItem)
        }

    }
}