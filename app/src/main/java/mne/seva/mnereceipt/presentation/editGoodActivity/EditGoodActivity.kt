package mne.seva.mnereceipt.presentation.editGoodActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityEditGoodBinding
import mne.seva.mnereceipt.presentation.ReceiptApplication


class EditGoodActivity : AppCompatActivity() {

    private val viewModel: EditGoodVM by viewModels {
        EditGoodVmFactory((application as ReceiptApplication).repository)
    }

    private val viewIdToGroupDbId = mutableMapOf<Int, Long>()
    private val groupDBIdToViewId = mutableMapOf<Long, Int>()

    private val goodViewIdToDBid = mutableMapOf<Int, Long>()
    private val goodDBidToViewId = mutableMapOf<Long, Int>()

    private lateinit var binding: ActivityEditGoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            binding.btnChange.isEnabled = savedInstanceState.getBoolean(COMMIT_BUTTON_ENABLED)
        }

        binding.changeNameBtn.setOnClickListener {
            val editNameDialog = EditGoodDialog.newInstance(editName = true)
            val fTrans = supportFragmentManager.beginTransaction()
            editNameDialog.show(fTrans, EditGoodDialog.TAG)
        }

        binding.addNewGroupBtn.setOnClickListener {
            val editNameDialog = EditGoodDialog.newInstance(editName = false)
            val fTrans = supportFragmentManager.beginTransaction()
            editNameDialog.show(fTrans, EditGoodDialog.TAG)
        }

        val nameId: Long = intent.getLongExtra("id", 0)
        viewModel.initNameId(nameId)

        val nameStr: String = intent.getStringExtra("name") ?: ""
        if (savedInstanceState == null) {
            viewModel.initName(nameStr)
        } else {
            viewModel.setNewName(savedInstanceState.getString(GOOD_NAME) ?: "")
        }

        viewModel.name.observe(this) {
            binding.editGoodNameLabel.text = getString(R.string.editGoodName_activity_title, it)
        }

        viewModel.listOrigNames.observe(this) {
            binding.origNamesRadioGroup.removeAllViews()
            val onlyOneName = (it.size == 1)
            for (good in it) {
                val rb = RadioButton(this)
                val viewId = View.generateViewId()
                goodViewIdToDBid[viewId] = good.id
                goodDBidToViewId[good.id] = viewId
                rb.id = viewId
                val nameWithPrice = good.name + " " + getString(R.string.goodPrice, good.price)
                rb.text = nameWithPrice
                if (onlyOneName) {
                    rb.isChecked = true
                    binding.btnChange.isEnabled = true
                }
                rb.setOnClickListener {
                    binding.btnChange.isEnabled = true
                }
                binding.origNamesRadioGroup.addView(rb)
            }

            if (savedInstanceState != null) {
                val selectedGoodDBId = savedInstanceState.getLong(SELECTED_NAME_ID)
                val selectedGoodViewId = goodDBidToViewId[selectedGoodDBId]
                if (selectedGoodViewId != null) {
                    val selectedRB = findViewById<RadioButton>(selectedGoodViewId)
                    selectedRB.isChecked = true
                }
            }
        }

        viewModel.listGroups.observe(this) {
            binding.groupsRadioGroup.removeAllViews()
            for (group in it.first) {
                val rb = RadioButton(this)
                val viewId = View.generateViewId()
                viewIdToGroupDbId[viewId] = group.id
                groupDBIdToViewId[group.id] = viewId
                rb.id = viewId
                rb.text = group.name
                binding.groupsRadioGroup.addView(rb)
            }

            val selectedGroupDbId = savedInstanceState?.getLong(SELECTED_GROUP_ID) ?: it.second
            val selectedGroupViewId = groupDBIdToViewId[selectedGroupDbId]
            if (selectedGroupViewId != null) {
                val selectedRB = findViewById<RadioButton>(selectedGroupViewId)
                selectedRB.isChecked = true
            }
        }

        viewModel.unit.observe(this) {
            if (savedInstanceState != null) {
                val selectedUnitView = findViewById<RadioButton>(savedInstanceState.getInt(SELECTED_UNIT_BTN_ID))
                selectedUnitView.isChecked = true
            } else {
                when (it) {
                    "kg" -> binding.kg.isChecked = true
                    "L" -> binding.L.isChecked = true
                    "pc" -> binding.pc.isChecked = true
                }
            }
        }

        viewModel.closeActivity.observe(this) {
            Toast.makeText(applicationContext, getString(R.string.good_changed_toast), Toast.LENGTH_LONG).show()
            finish()
        }

        binding.btnChange.setOnClickListener {
            val groupViewId = binding.groupsRadioGroup.checkedRadioButtonId
            val groupDbId = viewIdToGroupDbId[groupViewId]

            val nameViewId = binding.origNamesRadioGroup.checkedRadioButtonId
            val nameDbId = goodViewIdToDBid[nameViewId]

            val suffix = when (binding.unitRadioGroup.checkedRadioButtonId) {
                R.id.L -> "L"
                R.id.kg -> "kg"
                R.id.pc -> "pc"
                else -> ""
            }

            if (nameDbId != null && groupDbId != null) {
                viewModel.updateGood(goodId = nameDbId, groupId = groupDbId, suffix = suffix)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        val groupViewId = binding.groupsRadioGroup.checkedRadioButtonId
        val groupDbId = viewIdToGroupDbId[groupViewId]

        val nameViewId = binding.origNamesRadioGroup.checkedRadioButtonId
        val nameDbId = goodViewIdToDBid[nameViewId]

        val unitId = binding.unitRadioGroup.checkedRadioButtonId

        if (groupDbId != null) {
            outState.putLong(SELECTED_GROUP_ID, groupDbId)
        }

        if (nameDbId != null) {
            outState.putLong(SELECTED_NAME_ID, nameDbId)
        }

        outState.putInt(SELECTED_UNIT_BTN_ID, unitId)
        outState.putBoolean(COMMIT_BUTTON_ENABLED, binding.btnChange.isEnabled)

        outState.putString(GOOD_NAME, viewModel.name.value)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val SELECTED_GROUP_ID = "selected_group"
        const val SELECTED_NAME_ID = "selected_name"
        const val SELECTED_UNIT_BTN_ID = "selected_unit"
        const val COMMIT_BUTTON_ENABLED = "enable_button"
        const val GOOD_NAME = "good_name"
    }

}