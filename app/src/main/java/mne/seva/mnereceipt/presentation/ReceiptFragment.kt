package mne.seva.mnereceipt.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.FragmentReceiptBinding
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.presentation.addReceiptActivity.AddReceiptActivity
import mne.seva.mnereceipt.presentation.addReceiptActivity.AddShopDialog
import mne.seva.mnereceipt.presentation.addReceiptActivity.NewGoodsAdapter
import mne.seva.mnereceipt.presentation.addReceiptActivity.ReceiptAddViewModel
import mne.seva.mnereceipt.presentation.addReceiptActivity.ReceiptAddVmFactory


@Suppress("KDocUnresolvedReference")
class ReceiptFragment : Fragment() {
    /**
     * @param showOnly is for hiding buttons
     * Fragment for receipt displaying (both when add and view from DB)
     * он у нас "и швец, и жнец, и на дуде игрец" :)
     */

    private val argReceipt = "receipt"
    private val argShowOnly = "showOnly"

    interface CloseReceipt {
        fun closeReceiptFragment()  //need for closing activity after successful adding new receipt case
    }

    private lateinit var receipt: DownloadedReceipt
    private var showOnly: Boolean = false

    private val viewModel: ReceiptAddViewModel by activityViewModels {
        ReceiptAddVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            receipt = if (Build.VERSION.SDK_INT >= 33) {
                it.getSerializable(argReceipt, DownloadedReceipt::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(argReceipt)
            } as DownloadedReceipt
            showOnly = it.getBoolean(argShowOnly)
        }
        viewModel.setReceipt(receipt = receipt, showOnly = showOnly)

        if (!showOnly) {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val backFrag = BackDialog()
                backFrag.show(childFragmentManager, "tag")
            }
        }
    }

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showOnly) {
            binding.label.visibility = View.GONE
        }

        viewModel.shopName.observe(viewLifecycleOwner) {
            binding.shopName.text = it
        }

        viewModel.dateTime.observe(viewLifecycleOwner) {
            binding.date.text = getString(R.string.date_time_receipt, it)
        }

        viewModel.total.observe(viewLifecycleOwner) {
            binding.total.text = getString(R.string.total_receipt, it)
        }

        viewModel.showButtonShop.observe(viewLifecycleOwner) {
            if (it && !showOnly) {
                binding.edit.visibility = View.VISIBLE
            } else {
                binding.edit.visibility = View.GONE
            }
        }
        binding.edit.setOnClickListener {
            val dialog = AddShopDialog()
            dialog.show(parentFragmentManager, "tag1")
        }

        viewModel.byCash.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.cashImage.visibility = View.GONE
                binding.cardImage.visibility = View.GONE
            } else {
                if (it) {
                    binding.cashImage.visibility = View.VISIBLE
                    binding.cardImage.visibility = View.GONE
                } else {
                    binding.cashImage.visibility = View.GONE
                    binding.cardImage.visibility = View.VISIBLE
                }
            }
        }

        val adapter = NewGoodsAdapter(
            view.context,
            viewModel.listGoods.value ?: emptyList(),
            onlyShow = showOnly
        )
        viewModel.listGoods.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
            viewModel.checkAllGoods()
        }

        binding.goodList.adapter = adapter

        viewModel.addReceiptButton.observe(viewLifecycleOwner) {
            if (showOnly) {
                binding.ok.visibility = View.GONE
            } else {
                binding.ok.isEnabled = it
                if (it) {
                    binding.ok.text = getString(R.string.add_receipt_btn_ok_text)
                }
            }
        }
        binding.ok.setOnClickListener {
            viewModel.addReceipt()
        }

        viewModel.showShopNameFail.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.failNameShown()
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.empty_name_fail_toast),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.closeActivity.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.activityClosed()
                Toast.makeText(requireActivity().applicationContext, getString(R.string.receipt_saved_toast), Toast.LENGTH_LONG).show()
                (requireActivity() as AddReceiptActivity).closeReceiptFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(receipt: DownloadedReceipt, showOnly: Boolean) = ReceiptFragment().apply {
            arguments = Bundle().apply {
                putSerializable(argReceipt, receipt)
                putBoolean(argShowOnly, showOnly)
            }
        }
    }
}