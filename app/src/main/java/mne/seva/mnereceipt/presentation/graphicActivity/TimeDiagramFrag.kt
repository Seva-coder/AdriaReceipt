package mne.seva.mnereceipt.presentation.graphicActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mne.seva.mnereceipt.databinding.FragmentTimeDiagramBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication

class TimeDiagramFrag : Fragment() {

    private val viewModel: GraphicActivityVM by activityViewModels {
        GraphicActivityVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentTimeDiagramBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimeDiagramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManagerDays = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerDays.stackFromEnd = true
        binding.barViewDays.layoutManager = layoutManagerDays

        val layoutManagerWeeks = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerWeeks.stackFromEnd = true
        binding.barViewWeeks.layoutManager = layoutManagerWeeks

        val layoutManagerMonth = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerMonth.stackFromEnd = true
        binding.barViewMonth.layoutManager = layoutManagerMonth

        val daysAdapter = BarAdapter()
        viewModel.listCostsByDates.observe(viewLifecycleOwner) {
            daysAdapter.submitList(it)
        }
        binding.barViewDays.adapter = daysAdapter

        val weeksAdapter = BarAdapter()
        viewModel.listCostsByWeeks.observe(viewLifecycleOwner) {
            weeksAdapter.submitList(it)
        }
        binding.barViewWeeks.adapter = weeksAdapter

        val monthAdapter = BarAdapter()
        viewModel.listCostsByMonths.observe(viewLifecycleOwner) {
            monthAdapter.submitList(it)
        }
        binding.barViewMonth.adapter = monthAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}