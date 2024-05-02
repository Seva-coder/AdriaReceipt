package mne.seva.mnereceipt.presentation.goodsActivity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.databinding.GoodRowBinding

class GoodsViewHolder(val binding: GoodRowBinding, private val goodsActivity: GoodsActivity) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
        binding.btnEdit.setOnClickListener {
            goodsActivity.editGoodId(id = nameId, name = this.name)
        }
    }

    private var nameId: Long = 0
    private var name: String = ""
    fun bind(name: String, id: Long) {
        binding.goodName.text = name
        this.name = name
        nameId = id
    }

    override fun onClick(p0: View?) {
        goodsActivity.showGoodStat(id = nameId, name = name)
    }

    interface CallBackFromAdapter {
        fun showGoodStat(id: Long, name: String)
        fun editGoodId(id: Long, name: String)
    }

}