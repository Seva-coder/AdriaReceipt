package mne.seva.mnereceipt.presentation.addReceiptActivity

import android.widget.ArrayAdapter
import mne.seva.mnereceipt.domain.models.NewGood
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ReceiptAddRowBinding


class NewGoodsAdapter(private val context_loc: Context, list: List<NewGood>, val onlyShow: Boolean) :
    ArrayAdapter<NewGood>(context_loc, R.layout.receipt_add_row, list) {

    private var COLOR_GOOD_EXIST = 0
    private var backColor = 0
    private var COLOR_OK = 0
    private var COLOR_BAD = 0

    init {
        val typedValue = TypedValue()
        context_loc.theme.resolveAttribute(R.attr.colorGoodExist, typedValue, true)
        COLOR_GOOD_EXIST = ContextCompat.getColor(context_loc, typedValue.resourceId)

        context_loc.theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
        backColor = ContextCompat.getColor(context_loc, typedValue.resourceId)

        context_loc.theme.resolveAttribute(R.attr.colorItemSetted, typedValue, true)
        COLOR_OK = ContextCompat.getColor(context_loc, typedValue.resourceId)

        context_loc.theme.resolveAttribute(R.attr.colorItemNotSetted, typedValue, true)
        COLOR_BAD = ContextCompat.getColor(context_loc, typedValue.resourceId)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        var convertView = convertView

        val binding = if (convertView == null) {
            val binding = ReceiptAddRowBinding.inflate(LayoutInflater.from(context_loc), parent, false)
            convertView = binding.root
            convertView.tag = binding
            binding
        } else {
            convertView.tag as ReceiptAddRowBinding
        }

        val good = getItem(position)
        if (good!!.newNameSetted) {
            binding.name.text = good.newName
        } else {
            binding.name.text = good.nameOrig
        }

        binding.quantity.text = context_loc.getString(R.string.quantityInReceipt, good.quantity, good.suffix)
        binding.price.text = context_loc.getString(R.string.euroPerKg, good.price, good.suffix)
        binding.total.text = context_loc.getString(R.string.goodPrice, good.total)

        if (onlyShow) {
            binding.groupBtn.visibility = View.GONE
            binding.nameBtn.visibility = View.GONE
            binding.suffixBtn.visibility = View.GONE
        } else {

            binding.groupBtn.setOnClickListener {
                val groupDialog = AddGroupDialog.newInstance(position)
                val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
                groupDialog.show(ft, AddGroupDialog.TAG)
            }

            binding.nameBtn.setOnClickListener {
                val nameDialog = AddGoodNameDialog.newInstance(position)
                val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
                nameDialog.show(ft, AddGoodNameDialog.TAG)
            }

            binding.suffixBtn.setOnClickListener {
                val suffixDialog = SelectSuffixDialog.newInstance(position)
                val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
                suffixDialog.show(ft, SelectSuffixDialog.TAG)
            }

            if (good.goodId != 0L || (good.newNameSetted && good.groupSetted && good.suffixSetted)) {
                binding.groupBtn.visibility = View.GONE
                binding.nameBtn.visibility = View.GONE
                binding.suffixBtn.visibility = View.GONE
                convertView.setBackgroundColor(COLOR_GOOD_EXIST)
            } else {
                binding.groupBtn.visibility = View.VISIBLE
                binding.nameBtn.visibility = View.VISIBLE
                binding.suffixBtn.visibility = View.VISIBLE

                convertView.setBackgroundColor(backColor)

                if (good.groupSetted) {
                    binding.groupBtn.setBackgroundColor(COLOR_OK)
                } else {
                    binding.groupBtn.setBackgroundColor(COLOR_BAD)
                }

                if (good.suffixSetted) {
                    binding.suffixBtn.setBackgroundColor(COLOR_OK)
                } else {
                    binding.suffixBtn.setBackgroundColor(COLOR_BAD)
                }

                if (good.newNameSetted) {
                    binding.nameBtn.setBackgroundColor(COLOR_OK)
                } else {
                    binding.nameBtn.setBackgroundColor(COLOR_BAD)
                }
            }
        }

        return convertView
    }

}