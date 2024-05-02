package mne.seva.mnereceipt.presentation.goodsActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import mne.seva.mnereceipt.databinding.ActivityGoodsBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.editGoodActivity.EditGoodActivity
import mne.seva.mnereceipt.presentation.goodStatActivity.GoodStatActivity


class GoodsActivity : AppCompatActivity(), GoodsViewHolder.CallBackFromAdapter {

    private val viewModel: GoodsActivityVM by viewModels {
        GoodsActivityVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityGoodsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = GoodsAdapter(this)

        viewModel.listAllGoods.observe(this) {
            adapter.submitList(it)
        }

        binding.recycler.adapter = adapter
    }

    override fun showGoodStat(id: Long, name: String) {
        val intent = Intent(this, GoodStatActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    override fun editGoodId(id: Long, name: String) {
        val intent = Intent(this, EditGoodActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}