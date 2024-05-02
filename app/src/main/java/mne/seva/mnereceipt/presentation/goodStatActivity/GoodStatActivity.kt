package mne.seva.mnereceipt.presentation.goodStatActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityGoodStatBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class GoodStatActivity : AppCompatActivity() {

    private val viewModel: GoodStatVM by viewModels {
        GoodStatVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityGoodStatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodStatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id: Long = intent.getLongExtra("id", 0)
        viewModel.setNameId(id)
        val name: String = intent.getStringExtra("name") ?: ""

        binding.textGoodName.text = getString(R.string.goods_name, name)

        viewModel.listNames.observe(this) { namesWithId ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, namesWithId.map{it.name + " " + getString(R.string.goodPrice, it.price)} )
            binding.origNamesList.adapter = adapter
        }

        viewModel.group.observe(this) {
            binding.group.text = getString(R.string.group_name, it)
        }

        viewModel.cost7.observe(this) {
            binding.days7.text = getString(R.string.costsPerDay7, it, it / 7)
        }

        viewModel.cost30.observe(this) {
            binding.days30.text = getString(R.string.costsPerDay30, it, it / 30)
        }

        viewModel.priceList.observe(this) { priceList1 ->
            val minPricePosition = priceList1.indexOf(priceList1.minByOrNull { it.price })
            val adapter = ShopStatAdapter(this, priceList1, minPricePosition)
            binding.priceList.adapter = adapter
        }

    }
}