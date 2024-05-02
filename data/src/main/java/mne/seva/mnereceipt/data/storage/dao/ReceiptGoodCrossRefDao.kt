package mne.seva.mnereceipt.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import mne.seva.mnereceipt.data.storage.entities.ReceiptGoodCrossRef


@Dao
interface ReceiptGoodCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(receiptGoodCrossRef: ReceiptGoodCrossRef)
}