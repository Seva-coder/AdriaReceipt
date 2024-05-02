package mne.seva.mnereceipt.data.storage.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
class Shop(
    @PrimaryKey(autoGenerate = true) val shop_id: Long,
    val name_orig: String,
    val short_name: String
)