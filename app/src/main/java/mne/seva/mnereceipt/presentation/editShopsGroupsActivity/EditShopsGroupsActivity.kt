package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityEditShopsBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class EditShopsGroupsActivity : AppCompatActivity(), ShopViewHolder.EditShopCallBack,
    GroupViewHolder.EditGroupCallback {

    private val viewModel: EditShopsVM by viewModels {
        EditShopsVmFactory((application as ReceiptApplication).repository)
    }

    private lateinit var binding: ActivityEditShopsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val adapter = PageAdapterEdit(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.reduceDragSensitivity()

        val tabNames = arrayOf(getString(R.string.edit_shops_name_tab), getString(R.string.edit_groups_name_tab))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.showFailShopToast.collectLatest {
                    if (it) {
                        Toast.makeText(applicationContext, getString(R.string.shop_renamed_success_toast), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.change_name_exist_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.showFailGroupToast.collectLatest {
                    if (it) {
                        Toast.makeText(applicationContext, getString(R.string.group_renamed_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }


    override fun showEditShopDialog(position: Int) {
        val id = viewModel.listShops.value?.get(position)?.id ?: 0L
        val name = viewModel.listShops.value?.get(position)?.origName ?: "null name?"

        val dialog = EditShopDialog.newInstance(id = id, origName = name)
        dialog.show(supportFragmentManager, EditShopDialog.TAG)
    }

    override fun showEditGroupDialog(position: Int) {
        val id = viewModel.listGroups.value?.get(position)?.id ?: 0L
        val name = viewModel.listGroups.value?.get(position)?.name ?: "null name"

        val dialog = EditGroupDialog.newInstance(id = id, oldName = name)
        dialog.show(supportFragmentManager, EditGroupDialog.TAG)
    }
}