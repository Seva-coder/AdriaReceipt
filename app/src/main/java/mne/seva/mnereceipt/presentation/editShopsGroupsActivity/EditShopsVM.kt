package mne.seva.mnereceipt.presentation.editShopsGroupsActivity


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.RenameShopById
import mne.seva.mnereceipt.domain.usecases.RenameGroupById

class EditShopsVM(private val renameShopById: RenameShopById,
                  private val renameGroupById: RenameGroupById,
                  repository: ReceiptRepositoryImpl) : ViewModel() {

    private val _showFailShopToast = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val showFailShopToast = _showFailShopToast.asSharedFlow()

    private val _showFailGroupToast = MutableSharedFlow<Boolean>(replay = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)
    val showFailGroupToast = _showFailGroupToast.asSharedFlow()

    val listShops = repository.listShops.asLiveData()

    val listGroups = repository.listGroups.asLiveData()

    fun setShopName(id: Long, newName: String) {
        viewModelScope.launch {
            val success = renameShopById.execute(shopId = id, newName = newName)
            if (success) {
                _showFailShopToast.emit(true)
            } else {
                _showFailShopToast.emit(false)
            }
        }
    }

    fun renameGroup(id: Long, newName: String) {
        viewModelScope.launch {
            renameGroupById.execute(id = id, newName = newName)
            _showFailGroupToast.emit(true)
        }
    }

}