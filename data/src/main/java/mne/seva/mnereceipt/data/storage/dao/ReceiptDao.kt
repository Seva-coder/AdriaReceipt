package mne.seva.mnereceipt.data.storage.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mne.seva.mnereceipt.data.storage.entities.Receipt
import mne.seva.mnereceipt.domain.models.ReceiptWithShop
import mne.seva.mnereceipt.domain.models.CostByPeriod

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(receipt: Receipt): Long

    @Query("SELECT EXISTS (SELECT * FROM receipts WHERE iic = :iic AND tin = :tin)")
    suspend fun isReceiptExists(iic: String, tin: String) : Boolean

    @Query("SELECT receipt_id AS receiptId, short_name AS shopName, date_time AS date, total AS total, by_cash AS byCash, currency_type AS currencyType FROM receipts JOIN shops ON shops.shop_id = receipts.shop_id ORDER BY date_time DESC")
    fun getAllReceipts(): LiveData<List<ReceiptWithShop>>

    @Query("SELECT receipt_id AS receiptId, short_name AS shopName, date_time AS date, total AS total, by_cash AS byCash, currency_type AS currencyType FROM receipts JOIN shops ON shops.shop_id = receipts.shop_id ORDER BY date_time ASC")
    suspend fun exportAllReceipts(): List<ReceiptWithShop>

    @Query("SELECT receipt_id AS receiptId, short_name AS shopName, date_time AS date, total AS total, by_cash AS byCash, currency_type AS currencyType FROM receipts JOIN shops ON shops.shop_id = receipts.shop_id WHERE receipts.receipt_id = :receiptId")
    suspend fun getReceiptById(receiptId: Long): ReceiptWithShop

    @Query("SELECT DATE(date_time, 'unixepoch', 'localtime') AS text, SUM(total) AS cost FROM receipts GROUP BY text ORDER BY date_time ASC")
    suspend fun getCostsListByDates(): List<CostByPeriod>

    @Query("SELECT strftime('%Y-%W', date_time, 'unixepoch', 'localtime') AS text, SUM(total) AS cost FROM receipts GROUP BY text ORDER BY date_time ASC")
    suspend fun getCostsListByWeeks(): List<CostByPeriod>

    @Query("SELECT strftime('%Y-%m', date_time, 'unixepoch', 'localtime') AS text, SUM(total) AS cost FROM receipts GROUP BY text ORDER BY date_time ASC")
    suspend fun getCostsListByMonths(): List<CostByPeriod>

    @Query("SELECT strftime('%Y-%m', min(date_time), 'unixepoch', 'localtime') FROM receipts")
    suspend fun getMinReceiptTime(): String?

    @Query("SELECT strftime('%Y-%m', max(date_time), 'unixepoch', 'localtime') FROM receipts")
    suspend fun getMaxReceiptTime(): String?

}