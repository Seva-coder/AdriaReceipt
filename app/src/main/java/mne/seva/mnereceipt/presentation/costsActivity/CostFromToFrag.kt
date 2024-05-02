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
import mne.seva.mnereceipt.databinding.FragmentCostFromToBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.mainActivity.DialogFail


class CostFromToFrag : Fragment() {

    private val viewModel: CostsActivityVM by activityViewModels {
        CostsVMFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentCostFromToBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCostFromToBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.betweenDaysRes.text = getString(R.string.costsPerDay, 0f, 0f)

        viewModel.costBetween.observe(viewLifecycleOwner) {
            binding.betweenDaysRes.text = getString(R.string.costsPerDay, it.first, it.second)
        }

        viewModel.btnFromText.observe(viewLifecycleOwner) {
            binding.btnFrom.text = it
        }

        binding.btnFrom.setOnClickListener {
            val newFragment = DateFromDialog()
            newFragment.show(childFragmentManager, "dateFromPicker")
        }

        viewModel.btnToText.observe(viewLifecycleOwner) {
            binding.btnTo.text = it
        }

        binding.btnTo.setOnClickListener {
            val newFragment = DateToDialog()
            newFragment.show(childFragmentManager, "dateToPicker")
        }

        viewModel.dateNotValid.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.notValidDateShown()
                val dialogFail =
                    DialogFail.newInstance(title = getString(R.string.wrong_date_title), message = getString(
                                            R.string.dates_overlap_message))
                dialogFail.show(childFragmentManager, DialogFail.TAG)
            }
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = GoodsRowAdapter()

        viewModel.listGoodsBetweenDays.observe(viewLifecycleOwner) {
            adapter.days = it.second
            adapter.submitList(it.first)
        }

        binding.recycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}