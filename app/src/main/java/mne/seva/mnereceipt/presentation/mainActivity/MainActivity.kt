package mne.seva.mnereceipt.presentation.mainActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.R
import mne.seva.mnereceipt.databinding.ActivityMainBinding
import mne.seva.mnereceipt.presentation.AboutActivity
import mne.seva.mnereceipt.presentation.ReceiptApplication
import mne.seva.mnereceipt.presentation.addReceiptActivity.AddReceiptActivity
import mne.seva.mnereceipt.presentation.costsActivity.CostsActivity
import mne.seva.mnereceipt.presentation.editShopsGroupsActivity.EditShopsGroupsActivity
import mne.seva.mnereceipt.presentation.goodsActivity.GoodsActivity
import mne.seva.mnereceipt.presentation.graphicActivity.GraphicActivity
import mne.seva.mnereceipt.presentation.manualAdd.ManualReceiptAddActivity
import mne.seva.mnereceipt.presentation.scanActivity.ScanActivity
import mne.seva.mnereceipt.presentation.settingsActivity.SettingsActivity
import mne.seva.mnereceipt.presentation.viewReceiptsAct.ViewReceiptsActivity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository = (application as ReceiptApplication).repository,
                            fileRepository = (application as ReceiptApplication).fileRepository
        )
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val returnedCode = result.data?.getStringExtra("result_string") ?: ""
            if (returnedCode != "") {
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                val defaultName = preferences.getBoolean("name_from_receipt", false)
                val defaultGroup = preferences.getBoolean("use_default_group", false)
                val defaultUnit = preferences.getBoolean("use_default_unit", false)
                viewModel.loadReceipt(link = returnedCode,
                    defaultName = defaultName,
                    defaultGroup = defaultGroup,
                    defaultUnit = defaultUnit)
            }
        }
    }

    private val receiptsSafLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        uri?.let {
            viewModel.saveReceiptsToFile(uri)
        }
    }

    private fun showLoadingDialog() {
        val ft = supportFragmentManager.beginTransaction()
        val frg = supportFragmentManager.findFragmentByTag(ReceiptLoadingDialog.TAG)
        if (frg == null) {
            val loadingDialog = ReceiptLoadingDialog()
            loadingDialog.show(supportFragmentManager, ReceiptLoadingDialog.TAG)
        }
        ft.commit()
    }

    private fun hideLoadingDialog() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(10)  // case when exception will be immediately after dialog shown
            val ft = supportFragmentManager.beginTransaction()
            val frg = supportFragmentManager.findFragmentByTag(ReceiptLoadingDialog.TAG)
            if (frg != null) {
                (frg as ReceiptLoadingDialog).dismiss()
            }
            ft.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_help) {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        if (item.itemId == R.id.action_export) {
            val exportDialog = ExportDialog()
            exportDialog.show(supportFragmentManager, "exportDialog")
        }
        return true
    }

    fun exportReceiptsCsv() {
        val currentTime = LocalDateTime.now(ZoneOffset.systemDefault())
        val timeStr = currentTime.format(DateTimeFormatter.ofPattern("d MMM"))
        receiptsSafLauncher.launch(getString(R.string.export_receipts_filename, timeStr))
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.startLoading.collectLatest {
                    if (it) {
                        showLoadingDialog()
                    } else {
                        hideLoadingDialog()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.exported.collectLatest {
                    if (it) {
                        Toast.makeText(this@MainActivity,
                            getString(R.string.export_successful_toast), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.flowError.collectLatest {
                    val error = when (it) {
                        ErrorType.QR_EXIST -> DialogFail.newInstance(
                            title = getString(R.string.receipt_scanned_title_error),
                            message = getString(R.string.receipt_already_scanned_error_message)
                        )
                        ErrorType.INTERNET_FAILED -> DialogFail.newInstance(
                            title = getString(R.string.download_error_title),
                            message = getString(R.string.internet_lost_error_message)
                        )
                        ErrorType.QR_NOT_VALID -> DialogFail.newInstance(
                            title = getString(R.string.qr_invalid_title),
                            message = getString(R.string.field_absent_message)
                        )
                        ErrorType.QR_NO_LINK -> DialogFail.newInstance(
                            title = getString(R.string.wrong_link_title),
                            message = getString(R.string.wrong_link_description)
                        )
                        ErrorType.JSON_FAILED -> DialogFail.newInstance(
                            title = getString(R.string.download_error_title),
                            message = getString(R.string.wrong_json_error_message)
                        )
                        ErrorType.TIMEOUT -> DialogFail.newInstance(
                            title = getString(R.string.download_error_title),
                            message = getString(R.string.timeout_error_message)
                        )
                    }
                    error.show(supportFragmentManager, DialogFail.TAG)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.startAddActivity.collectLatest {
                    if (it) {
                        val intent = Intent(this@MainActivity, AddReceiptActivity::class.java)
                        intent.putExtra("receipt", viewModel.receipt)
                        startActivity(intent)
                    }
                }
            }
        }

        binding.addReceiptButton.setOnClickListener {
            tryOpenScanner()
        }

        binding.addManualBtn.setOnClickListener {
            startActivity(Intent(this, ManualReceiptAddActivity::class.java))
        }

        binding.viewCostsButton.setOnClickListener {
            startActivity(Intent(this, CostsActivity::class.java))
        }

        binding.goodsButton.setOnClickListener {
            startActivity(Intent(this, GoodsActivity::class.java))
        }

        binding.editShopButton.setOnClickListener {
            startActivity(Intent(this, EditShopsGroupsActivity::class.java))
        }

        binding.viewReceiptButton.setOnClickListener {
            startActivity(Intent(this, ViewReceiptsActivity::class.java))
        }

        binding.showGraphicsButton.setOnClickListener {
            startActivity(Intent(this, GraphicActivity::class.java))
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

    private fun tryOpenScanner() {
        if (!checkConnection()) {
            val internetDialog = InternetDialog()
            internetDialog.show(supportFragmentManager, "tag")
        } else {
            openQrScanner()
        }
    }

    fun openQrScanner() {
        val intent = Intent(this, ScanActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun checkConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        if (connectivityManager is ConnectivityManager) {
            return if (connectivityManager.activeNetwork != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                val capability = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                capability ?: false
            } else {
                false
            }
        }
        return false
    }

}
