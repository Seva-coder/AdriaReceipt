package mne.seva.mnereceipt.data.storage.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "receipts",
    foreignKeys = [
        ForeignKey(entity = Shop::class,
            parentColumns = arrayOf("shop_id"),
            childColumns = arrayOf("shop_id"),
            onDelete = ForeignKey.SET_DEFAULT
        )
    ]
)
class Receipt(
    @ColumnInfo(name = "receipt_id") @PrimaryKey(autoGenerate = true) val receiptId: Long,
    val iic: String,
    val tin: String,
    @ColumnInfo(name = "date_time") val dateTimeUtc: Long,
    val total: Double,
    @ColumnInfo(name = "by_cash") val byCash: Boolean?,
    @ColumnInfo(name = "currency_type") val currencyType: String,
    @ColumnInfo(name = "shop_id", defaultValue = "1", index = true) val shopId: Long,
    val country: String
)
