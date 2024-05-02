package mne.seva.mnereceipt.presentation.editShopsGroupsActivity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import mne.seva.mnereceipt.databinding.FragmentEditShopOrGroupBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication

class EditGroupFrag : Fragment() {

    private val viewModel: EditShopsVM by activityViewModels {
        EditShopsVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentEditShopOrGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditShopOrGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity)
        binding.recycler.setEmptyView(binding.emptyTextLabel)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(DividerItemDecoration(binding.recycler.context, layoutManager.orientation))

        val adapter = GroupAdapter(activity as EditShopsGroupsActivity)

        viewModel.listGroups.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.recycler.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}