package mne.seva.mnereceipt.presentation.addReceiptActivity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityAddReceiptsBinding
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.ReceiptFragment


class AddReceiptActivity : AppCompatActivity(), ReceiptFragment.CloseReceipt {

    private val viewModel: ReceiptAddViewModel by viewModels {
        ReceiptAddVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityAddReceiptsBinding

    private val receiptName = "receipt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReceiptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receipt = if (intent.hasExtra(receiptName)) {
             if (Build.VERSION.SDK_INT >= 33) {
                intent.getSerializableExtra(receiptName, DownloadedReceipt::class.java)
            } else {
                 @Suppress("DEPRECATION")
                intent.getSerializableExtra(receiptName)
            } as DownloadedReceipt
        } else {
            null
        }

        if (receipt == null) {
            finish()
        } else {
            if (savedInstanceState == null) {
                // activity opened first time, fragment has to be created
                val viewReceiptFrag = ReceiptFragment.newInstance(receipt = receipt, showOnly = false)
                val fTrans = supportFragmentManager.beginTransaction()
                fTrans.add(R.id.fragment, viewReceiptFrag)
                fTrans.commit()
            } else {
                // fragment recreated automatically, but possible new shop name restored manually
                val name = savedInstanceState.getString(SHOP_NAME)
                if (name != null) {
                    viewModel.setShopName(name)
                }

            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        val shopName = viewModel.shopName.value
        if (shopName != null) {
            outState.putString(SHOP_NAME, shopName)
        }
        super.onSaveInstanceState(outState)
    }

    override fun closeReceiptFragment() {
        finish()
    }

    companion object {
        const val SHOP_NAME = "shop_name"
    }

}