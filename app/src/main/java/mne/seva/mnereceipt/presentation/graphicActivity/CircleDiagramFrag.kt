package mne.seva.mnereceipt.presentation.graphicActivity

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.FragmentCircleDiagramsBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.mainActivity.DialogFail


class CircleDiagramFrag : Fragment() {

    private val viewModel: GraphicActivityVM by activityViewModels {
        GraphicActivityVmFactory((requireActivity().application as ReceiptApplication).repository)
    }

    private var _binding: FragmentCircleDiagramsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCircleDiagramsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private val animatorForNdays: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var days7ready = false
        var days30ready = false

        viewModel.btnFromText.observe(viewLifecycleOwner) {
            binding.btnFrom.text = it
        }

        viewModel.btnToText.observe(viewLifecycleOwner) {
            binding.btnTo.text = it
        }

        binding.btnFrom.setOnClickListener {
            val newFragment = GraphicDateFromDialog()
            newFragment.show(childFragmentManager, "dateFromPicker")
        }

        binding.btnTo.setOnClickListener {
            val newFragment = GraphicDateToDialog()
            newFragment.show(childFragmentManager, "dateToPicker")
        }

        animator.duration = 800
        animator.startDelay = 400
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener {
            val value = it.animatedValue as Float
            binding.diagram7days.setAnimParam(value)
            binding.diagram30days.setAnimParam(value)
        }

        viewModel.listGroupCosts7days.observe(viewLifecycleOwner) {
            val names = it.map { value -> value.name }
            val values = it.map { value -> value.cost }

            binding.diagram7days.setGroups(names, values)
            days7ready = true
            if (days30ready && !viewModel.isAnimCompleted()) {  // for synchronous animation run
                animator.start()
                viewModel.animDone()
            } else if (viewModel.isAnimCompleted()) {
                animator.end()
            }
        }

        viewModel.listGroupCosts30days.observe(viewLifecycleOwner) {
            val names = it.map { value -> value.name }
            val values = it.map { value -> value.cost }

            binding.diagram30days.setGroups(names, values)
            days30ready = true
            if (days7ready && !viewModel.isAnimCompleted()) {
                animator.start()
                viewModel.animDone()
            } else if (viewModel.isAnimCompleted()) {
                animator.end()
            }
        }

        animatorForNdays.duration = 800
        animatorForNdays.startDelay = 0
        animatorForNdays.interpolator = AccelerateDecelerateInterpolator()

        animatorForNdays.addUpdateListener {
            val value = it.animatedValue as Float
            binding.diagramNdays.setAnimParam(value)
        }

        viewModel.listGroupCostsCustom.observe(viewLifecycleOwner) {
            val names = it.map { value -> value.name }
            val values = it.map { value -> value.cost }

            binding.diagramNdays.setGroups(names, values)
            if (viewModel.isAnimNdaysCompleted()) {
                animatorForNdays.end()
            } else {
                animatorForNdays.start()
            }
            viewModel.animNdaysDone()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.inputDateError.collectLatest {
                    if (it) {
                        val dialogFail =
                            DialogFail.newInstance(title = getString(R.string.wrong_date_title), message = getString(
                                R.string.dates_overlap_message))
                        dialogFail.show(childFragmentManager, DialogFail.TAG)
                    }
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        animator.cancel()
        animatorForNdays.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}