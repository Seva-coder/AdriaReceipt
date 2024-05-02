package mne.seva.mnereceipt.data.storage.dao


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mne.seva.mnereceipt.data.storage.entities.NewName
import mne.seva.mnereceipt.domain.models.NameWithId
import mne.seva.mnereceipt.domain.models.NameWithPriceId

@Dao
interface NewNameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(newName: NewName): Long

    @Delete
    suspend fun deleteNameById(name: NewName)

    @Query("SELECT name FROM new_names")
    fun getAllNames(): Flow<List<String>>

    @Query("SELECT name FROM goods JOIN new_names ON goods.name_id = new_names.name_id WHERE goods.good_id = :goodId")
    suspend fun getGoodNewName(goodId: Long): String

    @Query("SELECT name_id FROM new_names WHERE name = :name")
    suspend fun getNameId(name: String): Long?

    @Query("SELECT name_id AS id, name AS name FROM new_names ORDER BY name ASC")
    fun getAllNamesWithId(): LiveData<List<NameWithId>>

    @Query("SELECT good_id AS id, price AS price, name_orig AS name FROM goods WHERE name_id = :id")
    suspend fun getOrigNamesById(id: Long): List<NameWithPriceId>

    @Query("UPDATE new_names SET name = :newName WHERE name_id = :nameId")
    suspend fun updateName(nameId: Long, newName: String)

}