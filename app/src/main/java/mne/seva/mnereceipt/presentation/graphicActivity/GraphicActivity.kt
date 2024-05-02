package mne.seva.mnereceipt.presentation.graphicActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityGraphicNewBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class GraphicActivity : AppCompatActivity() {

    private val viewModel: GraphicActivityVM by viewModels {
        GraphicActivityVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityGraphicNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphicNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.costs_by_groups_tab)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.costs_by_time_tab)))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
                    0 -> CircleDiagramFrag()
                    else -> TimeDiagramFrag()
                }
                replaceFragment(fragment)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        replaceFragment(CircleDiagramFrag())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}