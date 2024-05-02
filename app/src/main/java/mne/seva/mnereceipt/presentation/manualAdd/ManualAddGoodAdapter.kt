package mne.seva.mnereceipt.presentation.manualAdd

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ReceiptRowManualBinding
import mne.seva.mnereceipt.domain.models.NewGood


class ManualAddGoodAdapter(private val context_loc: Context, private val vm: ManualReceiptAddVM)
    : ArrayAdapter<NewGood>(context_loc, R.layout.receipt_row_manual, vm.listGoods.value ?: ArrayList()) {

    private var COLOR_OK = 0
    private var COLOR_BAD = 0


    init {
        val typedValue = TypedValue()

        context_loc.theme.resolveAttribute(R.attr.colorItemSetted, typedValue, true)
        COLOR_OK = ContextCompat.getColor(context_loc, typedValue.resourceId)

        context_loc.theme.resolveAttribute(R.attr.colorItemNotSetted, typedValue, true)
        COLOR_BAD = ContextCompat.getColor(context_loc, typedValue.resourceId)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        var convertView = convertView

        val binding = if (convertView == null) {
            val binding = ReceiptRowManualBinding.inflate(LayoutInflater.from(context_loc), parent, false)
            convertView = binding.root
            convertView.tag = binding
            binding
        } else {
            convertView.tag as ReceiptRowManualBinding
        }


        val good = getItem(position)

        binding.nameBtn.setOnClickListener {
            val nameDialog = ChangeGoodNameDialog.newInstance(position)
            val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
            nameDialog.show(ft, ChangeGoodNameDialog.TAG)
        }

        binding.groupBtn.setOnClickListener {
            val groupDialog = ChangeGroupDialog.newInstance(position)
            val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
            groupDialog.show(ft, ChangeGroupDialog.TAG)
        }

        binding.priceBtn.setOnClickListener {
            val priceDialog = PriceDialog.newInstance(position)
            val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
            priceDialog.show(ft, PriceDialog.TAG)
        }

        binding.quantityBtn.setOnClickListener {
            val quantityDialog = QuantityDialog.newInstance(position)
            val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
            quantityDialog.show(ft, QuantityDialog.TAG)
        }

        binding.unitBtn.setOnClickListener {
            val unitDialog = UnitDialog.newInstance(position)
            val ft = (context_loc as AppCompatActivity).supportFragmentManager.beginTransaction()
            unitDialog.show(ft, UnitDialog.TAG)
        }

        binding.deleteBtn.setOnClickListener {
            vm.deleteGood(position)
        }

        if (good != null) {
            binding.nameTv.text = good.newName
            binding.priceTv.text = context_loc.getString(R.string.euroPerKg, good.price, good
                .suffix)
            binding.quantityTv.text = context_loc.getString(R.string.quantityInReceipt, good
                .quantity, good.suffix)

            if (good.groupSetted) {
                binding.groupBtn.setBackgroundColor(COLOR_OK)
            } else {
                binding.groupBtn.setBackgroundColor(COLOR_BAD)
            }

            if (good.suffixSetted) {
                binding.unitBtn.setBackgroundColor(COLOR_OK)
            } else {
                binding.unitBtn.setBackgroundColor(COLOR_BAD)
            }

            if (good.newNameSetted) {
                binding.nameBtn.setBackgroundColor(COLOR_OK)
            } else {
                binding.nameBtn.setBackgroundColor(COLOR_BAD)
            }

            if (good.price != 0.0) {
                binding.priceBtn.setBackgroundColor(COLOR_OK)
            } else {
                binding.priceBtn.setBackgroundColor(COLOR_BAD)
            }

            if (good.quantity != 0.0) {
                binding.quantityBtn.setBackgroundColor(COLOR_OK)
            } else {
                binding.quantityBtn.setBackgroundColor(COLOR_BAD)
            }
        }

        return convertView
    }

}