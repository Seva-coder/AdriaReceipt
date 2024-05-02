package mne.seva.mnereceipt.presentation.graphicActivity


import androidx.recyclerview.widget.RecyclerView

class BarViewHolder(private val view: BarView) : RecyclerView.ViewHolder(view) {

    fun bind(cost: Double, text: String) {
        view.setParams(cost = cost, text = text)
    }

}