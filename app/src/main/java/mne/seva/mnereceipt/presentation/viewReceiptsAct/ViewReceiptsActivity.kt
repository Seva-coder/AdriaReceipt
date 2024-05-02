package mne.seva.mnereceipt.presentation.viewReceiptsAct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityViewReceiptsBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.ReceiptFragment

class ViewReceiptsActivity : AppCompatActivity(), ReceiptViewHolder.CallBackFromAdapter,
ListReceiptsFragment.ListFragOnScr {

    private val viewModel: ViewReceiptVM by viewModels {
        ReceiptViewVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityViewReceiptsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewReceiptsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.showReceipt.observe(this) {
            if (it) {
                val viewReceiptFrag =
                    ReceiptFragment.newInstance(receipt = viewModel.receiptToOpen, showOnly = true)

                val fTrans = supportFragmentManager.beginTransaction()
                fTrans.addToBackStack(null)
                fTrans.replace(R.id.fragment, viewReceiptFrag)
                fTrans.commit()
            }
        }

    }

    override fun openReceipt(position: Int) {
        val id = viewModel.listReceipt.value?.get(position)?.receiptId ?: 0
        viewModel.openReceipt(receiptId = id)
    }

    override fun listOnScreen() {
        viewModel.receiptShown()
    }

}