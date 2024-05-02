package mne.seva.mnereceipt.presentation.mainActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.domain.exceptions.ReceiptAlreadyExistException
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.usecases.IsReceiptExistUseCase
import mne.seva.mnereceipt.domain.usecases.loaders.LoadMNEReceipt
import mne.seva.mnereceipt.domain.usecases.ReceiptProvider
import mne.seva.mnereceipt.domain.usecases.ReceiptProviderByLink
import mne.seva.mnereceipt.domain.usecases.loaders.ReceiptLoader
import org.json.JSONException
import java.io.IOException

enum class ErrorType {
    INTERNET_FAILED,
    TIMEOUT,
    JSON_FAILED,
    QR_NOT_VALID,
    QR_NO_LINK,
    QR_EXIST
}

class MainViewModel(private val isReceiptExistUseCase : IsReceiptExistUseCase,
                    private val loadMNEReceipt: LoadMNEReceipt,
                    private val receiptProviderByLink: ReceiptProviderByLink
) : ViewModel() {

    private val _sharedFlowError = MutableSharedFlow<ErrorType>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val flowError = _sharedFlowError.asSharedFlow()

    private val _startAddActivity = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val startAddActivity = _startAddActivity.asSharedFlow()

    private val _startLoading = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val startLoading = _startLoading.asSharedFlow()

    lateinit var receipt: DownloadedReceipt
    private lateinit var downloader: ReceiptLoader

    fun loadReceipt(link: String, defaultName: Boolean, defaultGroup: Boolean, defaultUnit: Boolean) {
        val provider = receiptProviderByLink.execute(link)
        if (provider == ReceiptProvider.NOT_EXIST) {
            _sharedFlowError.tryEmit(ErrorType.QR_NOT_VALID)
            return
        }

        downloader = when(provider) {  //NOT_EXIST not reachable here
            ReceiptProvider.MNE -> loadMNEReceipt
            else -> loadMNEReceipt
        }

        val rNum: ReceiptNumber? = downloader.linkToNumber(link)

        val handler = CoroutineExceptionHandler { _, e ->
            when(e) {
                is IOException -> {
                    if (e.message != "Socket closed" && e.message != "timeout") {
                        // case when user cancelled loading manually - not a error
                        _sharedFlowError.tryEmit(ErrorType.INTERNET_FAILED)
                    }
                    if (e.message == "timeout") {
                        _sharedFlowError.tryEmit(ErrorType.TIMEOUT)
                    }
                }
                is JSONException -> _sharedFlowError.tryEmit(ErrorType.JSON_FAILED)
                is ReceiptAlreadyExistException -> _sharedFlowError.tryEmit(ErrorType.QR_EXIST)
            }
            _startLoading.tryEmit(false)
        }

        if (rNum == null) {
            _sharedFlowError.tryEmit(ErrorType.QR_NOT_VALID)
        } else {
            viewModelScope.launch(handler) {
                _startLoading.emit(true)
                isReceiptExistUseCase.execute(rNum)
                receipt = downloader.download(number = rNum, defaultName = defaultName,
                    defaultGroup = defaultGroup, defaultUnit = defaultUnit)
                _startAddActivity.emit(true)
                _startLoading.emit(false)
            }
        }
    }

    fun stopDownloading() {
        downloader.stopDownload()
    }

}