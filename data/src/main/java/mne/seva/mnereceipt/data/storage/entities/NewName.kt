package mne.seva.mnereceipt.data.storage.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_names")
class NewName(
    @PrimaryKey(autoGenerate = true) val name_id: Long,
    val name: String
)