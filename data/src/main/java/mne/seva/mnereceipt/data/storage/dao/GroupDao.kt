package mne.seva.mnereceipt.data.storage.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mne.seva.mnereceipt.data.storage.entities.Group
import mne.seva.mnereceipt.domain.models.GroupCost

@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(groupDao: Group)

    @Query("SELECT group_id AS id, name AS name FROM groups")
    fun getAllGroup(): Flow<List<mne.seva.mnereceipt.domain.models.Group>>

    @Query("SELECT group_id FROM groups WHERE name = :name LIMIT 1")
    suspend fun getGroupId(name: String): Long?

    @Query("SELECT groups.name FROM groups JOIN goods ON groups.group_id = goods.group_id " +
            "JOIN new_names ON goods.name_id = new_names.name_id " +
            "WHERE new_names.name_id = :nameId")
    suspend fun getGroupNameByNameId(nameId: Long): String

    @Query("SELECT group_id AS id, name AS name FROM groups")
    suspend fun getAllGroupsList(): List<mne.seva.mnereceipt.domain.models.Group>

    @Query("SELECT groups.group_id AS id, groups.name AS name, SUM(goods.price * ReceiptGoodCrossRef.quantity) AS cost FROM " +
            "groups JOIN goods ON groups.group_id = goods.group_id JOIN " +
            "ReceiptGoodCrossRef ON goods.good_id = ReceiptGoodCrossRef.good_id JOIN " +
            "receipts ON ReceiptGoodCrossRef.receipt_id = receipts.receipt_id WHERE " +
            "receipts.date_time BETWEEN :dayFrom AND :dayTo " +
            "GROUP BY groups.group_id " +
            "ORDER BY cost DESC")
    suspend fun getGroupCostListFromTo(dayFrom: Long, dayTo: Long): List<GroupCost>

    @Query("UPDATE groups SET name = :newName WHERE group_id = :id")
    suspend fun renameGroupById(id: Long, newName: String)

}