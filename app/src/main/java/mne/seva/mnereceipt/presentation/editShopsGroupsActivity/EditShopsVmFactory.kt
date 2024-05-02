package mne.seva.mnereceipt.presentation.editShopsGroupsActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.RenameGroupById
import mne.seva.mnereceipt.domain.usecases.RenameShopById


class EditShopsVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val renameShopById by lazy { RenameShopById(receiptRepository = repository) }
    private val renameGroupById by lazy { RenameGroupById(receiptRepository = repository) }


    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditShopsVM(renameShopById = renameShopById,
            renameGroupById = renameGroupById,
            repository = repository) as T
    }

}