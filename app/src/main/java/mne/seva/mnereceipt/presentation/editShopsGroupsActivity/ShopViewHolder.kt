package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mne.seva.mnereceipt.databinding.ShopRowBinding


class ShopViewHolder(val binding: ShopRowBinding, private val editShopsActivity: EditShopsGroupsActivity) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(shopNameOrig: String, shopNameNew: String) {
        binding.shopNameNew.text = shopNameNew
        binding.shopNameOrig.text = shopNameOrig
    }

    interface EditShopCallBack {
        fun showEditShopDialog(position: Int)
    }

    override fun onClick(p0: View?) {
        editShopsActivity.showEditShopDialog(position = adapterPosition)
    }
}