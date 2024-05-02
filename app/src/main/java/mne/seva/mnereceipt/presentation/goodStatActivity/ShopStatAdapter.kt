package mne.seva.mnereceipt.presentation.goodStatActivity

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.GoodStatRowBinding
import mne.seva.mnereceipt.domain.models.ShopWithPrice
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ShopStatAdapter(private val context_loc: Context, list: List<ShopWithPrice>, private val minPricePosition: Int) :
    ArrayAdapter<ShopWithPrice>(context_loc, R.layout.good_stat_row, list) {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

    private var COLOR_MIN_PRICE = 0
    private var BACK_COLOR = 0
    init {
        val typedValue = TypedValue()
        context_loc.theme.resolveAttribute(R.attr.colorGoodExist, typedValue, true)
        COLOR_MIN_PRICE = ContextCompat.getColor(context_loc, typedValue.resourceId)

        context_loc.theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        BACK_COLOR = ContextCompat.getColor(context_loc, typedValue.resourceId)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        var convertView = convertView

        val binding = if (convertView == null) {
            val binding = GoodStatRowBinding.inflate(LayoutInflater.from(context_loc), parent, false)
            convertView = binding.root
            convertView.tag = binding
            binding
        } else {
            convertView.tag as GoodStatRowBinding
        }

        val row = getItem(position)
        if (position == minPricePosition) {
            convertView.setBackgroundColor(COLOR_MIN_PRICE)
        } else {
            convertView.setBackgroundColor(BACK_COLOR)
        }

        if (row != null) {
            binding.shopName.text = row.name

            val date = Instant.ofEpochSecond(row.date).atZone(ZoneOffset.systemDefault())
            binding.date.text = date.format(formatter)
            binding.cost.text = context_loc.getString(R.string.euroPerKg, row.price, row.suffix)
        }

        return convertView
    }

}