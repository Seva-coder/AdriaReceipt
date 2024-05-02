package mne.seva.mnereceipt.data.storage.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "goods",
    foreignKeys = [
        ForeignKey(entity = Group::class,
            parentColumns = arrayOf("group_id"),
            childColumns = arrayOf("group_id"),
            onDelete = ForeignKey.SET_DEFAULT
        ),
        ForeignKey(entity = NewName::class,
            parentColumns = arrayOf("name_id"),
            childColumns = arrayOf("name_id"),
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(entity = Shop::class,
            parentColumns = arrayOf("shop_id"),
            childColumns = arrayOf("shop_id"),
            onDelete = ForeignKey.SET_DEFAULT
        )
    ]
)
class Good(
    @ColumnInfo(name = "good_id") @PrimaryKey(autoGenerate = true) val goodId: Long,
    @ColumnInfo(name = "name_orig", index = true) val nameOrig: String,
    val price: Double,
    val code: String?,
    @ColumnInfo(name = "currency_type") val currencyType: String,
    @ColumnInfo(name = "group_id", defaultValue = "1", index = true) val groupId: Long,
    @ColumnInfo(name = "name_id", index = true) val nameId: Long,
    @ColumnInfo(name = "shop_id", defaultValue = "1", index = true) val shopId: Long,
    val suffix: String,
    val country: String
)