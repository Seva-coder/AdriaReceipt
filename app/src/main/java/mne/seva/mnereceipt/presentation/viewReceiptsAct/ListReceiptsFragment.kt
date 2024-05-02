package mne.seva.mnereceipt.presentation.viewReceiptsAct

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.fragment.app.activityViewModels
import mne.seva.mnereceipt.databinding.FragmentReceiptsListBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class ListReceiptsFragment : Fragment() {

    interface ListFragOnScr {
        fun listOnScreen()
    }

    private val viewModel: ViewReceiptVM by activityViewModels {
        ReceiptViewVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as ViewReceiptsActivity).listOnScreen()
    }

    private var _binding: FragmentReceiptsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReceiptsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = ReceiptAdapter(requireActivity() as ViewReceiptsActivity)

        viewModel.listReceipt.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.recycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}