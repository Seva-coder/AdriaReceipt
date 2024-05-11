package mne.seva.mnereceipt.presentation

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import mne.seva.mnereceipt.data.repository.NetworkRequests
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.data.storage.DbReceiptStorage
import mne.seva.mnereceipt.data.storage.FileRepositoryImpl
import mne.seva.mnereceipt.data.storage.ReceiptDatabase

class ReceiptApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { ReceiptDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ReceiptRepositoryImpl(
        receiptStorage = DbReceiptStorage(shopDao = database.shopDao(),
                                            groupDao = database.groupDao(),
                                            newNameDao = database.newNameDao(),
                                            receiptDao = database.receiptDao(),
                                            goodDao = database.goodDao(),
            receiptGoodCrossRefDao = database.receiptGoodCrossRefDao()),
        network = NetworkRequests()
    )}

    val fileRepository by lazy { FileRepositoryImpl(applicationContext) }
}