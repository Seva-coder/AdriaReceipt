package mne.seva.mnereceipt.presentation.editGoodActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.usecases.CreateNewGroupUseCase
import mne.seva.mnereceipt.domain.usecases.CreateNewName
import mne.seva.mnereceipt.domain.usecases.DeleteNameIfEmpty
import mne.seva.mnereceipt.domain.usecases.GetAllGroups
import mne.seva.mnereceipt.domain.usecases.GetGroupIdByGoodName
import mne.seva.mnereceipt.domain.usecases.GetNameId
import mne.seva.mnereceipt.domain.usecases.GetOrigNamesById
import mne.seva.mnereceipt.domain.usecases.GetUnitByNameId
import mne.seva.mnereceipt.domain.usecases.UpdateGood
import mne.seva.mnereceipt.domain.usecases.UpdateName


class EditGoodVmFactory(private val repository: ReceiptRepositoryImpl) : ViewModelProvider.Factory {

    private val getOrigNamesById by lazy { GetOrigNamesById(receiptRepository = repository) }
    private val getGroupIdByGoodName by lazy { GetGroupIdByGoodName(receiptRepository = repository) }
    private val getAllGroups by lazy { GetAllGroups(receiptRepository = repository) }
    private val getUnitByNameId by lazy { GetUnitByNameId(receiptRepository = repository) }
    private val createNewGroupUseCase by lazy { CreateNewGroupUseCase(receiptRepository = repository) }
    private val createNewName by lazy { CreateNewName(receiptRepository = repository) }
    private val updateGood by lazy { UpdateGood(receiptRepository = repository) }
    private val updateName by lazy { UpdateName(receiptRepository = repository) }
    private val getNameId by lazy { GetNameId(receiptRepository = repository) }
    private val deleteNameIfEmpty by lazy { DeleteNameIfEmpty(receiptRepository = repository) }

    override fun <T : ViewModel> create(modelClass : Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditGoodVM(getOrigNamesById = getOrigNamesById,
            getGroupIdByGoodName = getGroupIdByGoodName,
            repository = repository,
            getAllGroups = getAllGroups,
            getUnitByNameId = getUnitByNameId,
            createNewGroupUseCase = createNewGroupUseCase,
            createNewName = createNewName,
            updateGood = updateGood,
            updateName = updateName,
            getNameId = getNameId,
            deleteNameIfEmpty = deleteNameIfEmpty
        ) as T
    }

}