package mne.seva.mnereceipt.presentation.costsActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.FragmentCost7dayBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class Cost7dayFrag : Fragment() {

    private val viewModel: CostsActivityVM by activityViewModels {
        CostsVMFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentCost7dayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCost7dayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.days7res.text = getString(R.string.costsPerDay7, 0f, 0f)

        viewModel.cost7.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.days7res.text = getString(R.string.costsPerDay7, it, it / 7)
            }
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = GoodsRowAdapter()
        adapter.days = 7.0

        viewModel.listGoods7days.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.recycler.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}