package mne.seva.mnereceipt.presentation.costsActivity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityCostsBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication
import java.time.LocalDateTime


class CostsActivity : AppCompatActivity() {

    private val viewModel: CostsActivityVM by viewModels {
        CostsVMFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityCostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // case when activity killed, but bundle with groups saved
        if (!viewModel.groupsInited && savedInstanceState != null) {
            val dateFrom = if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getSerializable(DATE_FROM, LocalDateTime::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getSerializable(DATE_FROM)
            }

            val dateTo = if (Build.VERSION.SDK_INT >= 33) {
                savedInstanceState.getSerializable(DATE_TO, LocalDateTime::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState.getSerializable(DATE_TO)
            }

            if (dateFrom != null) {
                viewModel.setDateFrom(dateFrom as LocalDateTime)
            }

            if (dateTo != null) {
                viewModel.setDateTo(dateTo as LocalDateTime)
            }

            viewModel.loadCheckedGroupsFromBundle(savedInstanceState.getLongArray(SELECTED_GROUPS) ?: longArrayOf())
            viewModel.groupsInited = true
        }

        viewModel.listGroups.observe(this) { groupList ->

            groupList.forEach {
                val checkBox = CheckBox(this)
                checkBox.text = it.name

                checkBox.isChecked = if (viewModel.groupsInited) viewModel.checkedGroups.contains(it.id) else true
                checkBox.isSaveEnabled = false
                checkBox.setOnCheckedChangeListener { _, selected ->
                    if (selected) {
                        viewModel.selectGroupId(it.id)
                    } else {
                        viewModel.unSelectGroupTd(it.id)
                    }
                }
                binding.groups.addView(checkBox)
            }

            if (!viewModel.groupsInited) {
                viewModel.groupsInited = true
                viewModel.upd7and30days()  // group list already ready, so add all groups to checked
            }

        }

        /**
         * Reduces drag sensitivity of [ViewPager2] widget
         */
        fun ViewPager2.reduceDragSensitivity() {
            val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(this) as RecyclerView

            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop*6)       // "6" was obtained experimentally
        }

        val adapter = PageAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.reduceDragSensitivity()


        val tabNames = arrayOf(getString(R.string.days7_tab_name), getString(R.string.days30_tab_name), getString(R.string.by_period_tab_name))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLongArray(SELECTED_GROUPS, viewModel.checkedGroups.toLongArray())

        if (viewModel.dateFromSetted()) {
            outState.putSerializable(DATE_FROM, viewModel.dateFrom_)
        }

        if (viewModel.dateToSetted()) {
            outState.putSerializable(DATE_TO, viewModel.dateTo_)
        }

        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SELECTED_GROUPS = "selected_groups"
        const val DATE_FROM = "date_from"
        const val DATE_TO = "date_to"
    }

}