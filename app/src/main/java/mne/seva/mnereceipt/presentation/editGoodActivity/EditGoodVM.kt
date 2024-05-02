package mne.seva.mnereceipt.presentation.editGoodActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.repository.ReceiptRepositoryImpl
import mne.seva.mnereceipt.domain.models.Group
import mne.seva.mnereceipt.domain.models.NameWithPriceId
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

class EditGoodVM(
    private val getOrigNamesById: GetOrigNamesById,
    private val getGroupIdByGoodName: GetGroupIdByGoodName,
    private val getAllGroups: GetAllGroups,
    private val getUnitByNameId: GetUnitByNameId,
    private val createNewGroupUseCase: CreateNewGroupUseCase,
    private val createNewName: CreateNewName,
    private val updateGood: UpdateGood,
    private val updateName: UpdateName,
    private val getNameId: GetNameId,
    private val deleteNameIfEmpty: DeleteNameIfEmpty,
    val repository: ReceiptRepositoryImpl
) : ViewModel() {

    val listNewNames = repository.listNames

    private val _listGroups = MutableLiveData<Pair<List<Group>, Long>>()
    val listGroups: LiveData<Pair<List<Group>, Long>> = _listGroups

    private val _listOrigNames = MutableLiveData<List<NameWithPriceId>>()
    val listOrigNames: LiveData<List<NameWithPriceId>> = _listOrigNames

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private var goodInited = false

    private val _unit = MutableLiveData<String>()
    val unit: LiveData<String> = _unit

    private var nameId: Long = 0

    fun initNameId(nameId: Long) {
        if (!goodInited) {
            this.nameId = nameId
            goodInited = true
            viewModelScope.launch {
                this@EditGoodVM._listOrigNames.postValue(getOrigNamesById.execute(nameId))
                val selectedGroupId = getGroupIdByGoodName.execute(nameId)
                updateGroupsList(selectedGroupId)
                this@EditGoodVM._unit.postValue(getUnitByNameId.execute(nameId))
            }
        }
    }

    private suspend fun updateGroupsList(selectedGroupId: Long) {
        val groupList = getAllGroups.execute()
        _listGroups.postValue(Pair(groupList, selectedGroupId))
    }

    fun initName(name: String) {
        _name.value = name
    }

    private var nameWasChanged = false
    fun setNewName(name: String) {
        if (name != _name.value) {
            nameWasChanged = true
        }
        _name.value = name
    }

    fun setNewGroup(groupName: String) {
        viewModelScope.launch {
            val groupId = createNewGroupUseCase.execute(groupName)
            updateGroupsList(groupId)
        }
    }

    private val _closeActivity = MutableLiveData<Boolean>()
    val closeActivity: LiveData<Boolean> = _closeActivity

    fun updateGood(goodId: Long, groupId: Long, suffix: String) {
        if (goodId != -1L && groupId != -1L && nameId != -1L && suffix != "") {
            viewModelScope.launch {
                var possibleEmptyNameId = -1L
                if (nameWasChanged) {
                    val list = _listOrigNames.value
                    val newName = _name.value ?: "null name"
                    val existingNameId = getNameId.execute(name = newName)
                    if (existingNameId == -1L) {
                        // case when name not exist
                        if (list != null && list.size > 1) {
                            // case when MORE then 1 origName use this newName -
                            // crete new name and link THIS good to it
                            nameId = createNewName.execute(newName)
                        } else {
                            // case when exist only 1 origName which use this newName -
                            // just rename existing row in db
                            updateName.execute(nameId = nameId, newName = newName)
                        }
                    } else {
                        possibleEmptyNameId = nameId // name_id to possible deletion
                        nameId = existingNameId
                    }
                }

                updateGood.execute(
                    goodId = goodId,
                    groupId = groupId,
                    nameId = nameId,
                    suffix = suffix
                )

                if (possibleEmptyNameId != -1L) {
                    deleteNameIfEmpty.execute(nameId = possibleEmptyNameId)
                }

                _closeActivity.postValue(true)
            }
        }
    }

}