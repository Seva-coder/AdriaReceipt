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
import mne.seva.mnereceipt.databinding.FragmentCost30daysBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class Cost30dayFrag : Fragment() {

    private val viewModel: CostsActivityVM by activityViewModels {
        CostsVMFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentCost30daysBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentCost30daysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.days30res.text = getString(R.string.costsPerDay30, 0f, 0f)

        viewModel.cost30.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.days30res.text = getString(R.string.costsPerDay30, it, it / 30)
            }
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = GoodsRowAdapter()
        adapter.days = 30.0

        viewModel.listGoods30days.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.recycler.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}