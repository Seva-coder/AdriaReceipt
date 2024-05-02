package mne.seva.mnereceipt.data.storage.entities


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
class Group (
    @PrimaryKey(autoGenerate = true) val group_id: Long,
    val name: String
)