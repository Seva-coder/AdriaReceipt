package mne.seva.mnereceipt.presentation.manualAdd

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityManualReceiptAddBinding
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.presentation.BackDialog
import mne.seva.mnereceipt.presentation.ReceiptApplication
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ManualReceiptAddActivity : AppCompatActivity() {

    private val viewModel: ManualReceiptAddVM by viewModels {
        ManualVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityManualReceiptAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualReceiptAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            val shopName = savedInstanceState.getString(SHOP_NAME)

            val date = if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getSerializable(DATE, LocalDateTime::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getSerializable(DATE)
            } as LocalDateTime?

            val goods = (if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getSerializable(GOODS_LIST, ArrayList::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getSerializable(GOODS_LIST) as? ArrayList<*>
            })?.filterIsInstance<NewGood>() as ArrayList<NewGood>?

            if (shopName != null && shopName != "") {
                viewModel.setShopName(shopName)
            }

            if (date != null) {
                viewModel.setBuyDate(date)
            }

            if (goods != null) {
                viewModel.restoreGoodsList(goods)
            }

            viewModel.checkAllGoods()
        }

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorItemSetted, typedValue, true)
        val colorOK = ContextCompat.getColor(this, typedValue.resourceId)

        theme.resolveAttribute(R.attr.colorItemNotSetted, typedValue, true)
        val colorBAD = ContextCompat.getColor(this, typedValue.resourceId)

        binding.shopBtn.setOnClickListener {
            val shopDialog = AddShopNameDialog()
            shopDialog.show(supportFragmentManager, "shopDialog")
        }

        binding.dateBtn.setOnClickListener {
            val dateDialog = DateDialog()
            dateDialog.show(supportFragmentManager, "dateDialog")
        }

        binding.addBtn.setOnClickListener {
            viewModel.addNewGood()
        }

        binding.addReceiptBtn.setOnClickListener {
            val byCash = binding.cash.isChecked
            viewModel.addReceipt(byCash = byCash)
        }

        viewModel.shopName.observe(this) {
            binding.shopNameTv.text = it
        }

        viewModel.date.observe(this) {
            binding.dateTv.text = it.format(DateTimeFormatter.ofPattern("d MMM"))
        }

        val adapter = ManualAddGoodAdapter(context_loc = this, vm = viewModel)
        viewModel.listGoods.observe(this) {
            adapter.notifyDataSetChanged()
        }

        binding.goodList.adapter = adapter

        viewModel.addReceiptButton.observe(this) {
            binding.addReceiptBtn.isEnabled = it
        }

        viewModel.shopBtnOk.observe(this) {
            if (it) {
                binding.shopBtn.setBackgroundColor(colorOK)
            } else {
                binding.shopBtn.setBackgroundColor(colorBAD)
            }
        }

        viewModel.dateBtnOk.observe(this) {
            if (it) {
                binding.dateBtn.setBackgroundColor(colorOK)
            } else {
                binding.dateBtn.setBackgroundColor(colorBAD)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.closeActivity.collectLatest {
                    if (it) {
                        finish()
                        Toast.makeText(application, getString(R.string.receipt_saved_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback {
            val backFrag = BackDialog()
            backFrag.show(supportFragmentManager, "tag")
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        // nul will be checked on receiving
        outState.putSerializable(SHOP_NAME, viewModel.shopName.value)
        outState.putSerializable(DATE, viewModel.date.value)
        outState.putSerializable(GOODS_LIST, viewModel.listGoods.value)

        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SHOP_NAME = "shop_name"
        const val DATE = "date"
        const val GOODS_LIST = "goods_list"
    }
}