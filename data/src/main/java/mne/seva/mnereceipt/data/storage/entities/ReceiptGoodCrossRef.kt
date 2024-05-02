package mne.seva.mnereceipt.data.storage.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(primaryKeys = ["good_id", "receipt_id"],
    foreignKeys = [
        ForeignKey(entity = Receipt::class,
            parentColumns = arrayOf("receipt_id"),
            childColumns = arrayOf("receipt_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class ReceiptGoodCrossRef(
    @ColumnInfo(name = "good_id") val goodId: Long,
    @ColumnInfo(name = "receipt_id", index = true) val receiptId: Long,
    val quantity: Double
)