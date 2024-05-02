package mne.seva.mnereceipt.data.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mne.seva.mnereceipt.data.storage.entities.Good
import mne.seva.mnereceipt.data.storage.entities.GoodsInGroup
import mne.seva.mnereceipt.data.storage.entities.GoodWithQuantity


@Dao
interface GoodDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(good: Good): Long

    @Query("SELECT * FROM goods WHERE good_id = :goodId")
    suspend fun getGoodById(goodId: Long): Good

    @Query("SELECT good_id FROM goods WHERE code = :code AND shop_id = :shopId AND abs(price - :currPrice) < 0.001")
    suspend fun getExactGoodIdByCode(code: String, shopId: Long, currPrice: Double): Long?

    @Query("SELECT good_id FROM goods WHERE name_orig = :origName AND shop_id = :shopId AND abs(price - :currPrice) < 0.001")
    suspend fun getExactGoodIdByOrigName(origName: String, shopId: Long, currPrice: Double): Long?

    @Query("SELECT good_id FROM goods WHERE code = :code")
    suspend fun getProbGoodIdByCode(code: String): Long?

    @Query("SELECT good_id FROM goods WHERE name_orig = :origName")
    suspend fun getProbGoodIdByOrigName(origName: String): Long?

    @Query("SELECT new_names.name AS name," +
            "goods.price AS price," +
            "goods.suffix AS suffix," +
            "ReceiptGoodCrossRef.quantity AS quantity " +
            "FROM ReceiptGoodCrossRef JOIN goods ON ReceiptGoodCrossRef.good_id = goods.good_id " +
            "JOIN new_names ON goods.name_id = new_names.name_id " +
            "WHERE ReceiptGoodCrossRef.receipt_id = :receiptId")
    suspend fun getReceiptGoodList(receiptId: Long): List<GoodWithQuantity>


    @Query("SELECT SUM(goods.price * ReceiptGoodCrossRef.quantity) FROM " +
            "goods JOIN ReceiptGoodCrossRef ON goods.good_id = ReceiptGoodCrossRef.good_id JOIN " +
            "receipts ON ReceiptGoodCrossRef.receipt_id = receipts.receipt_id WHERE " +
            "receipts.date_time BETWEEN :unixTimeFrom AND :unixTimeTo AND goods.group_id IN (:groups)")
    suspend fun getCostBetweenDays(groups: LongArray, unixTimeFrom: Long, unixTimeTo: Long): Double?


    @Query("SELECT new_names.name_id AS nameId, new_names.name AS name, " +
            "goods.suffix AS suffix, SUM(ReceiptGoodCrossRef.quantity) AS quantity, " +
            "SUM(goods.price * ReceiptGoodCrossRef.quantity) AS total FROM " +
            "goods JOIN new_names ON goods.name_id = new_names.name_id JOIN " +
            "ReceiptGoodCrossRef ON goods.good_id = ReceiptGoodCrossRef.good_id JOIN " +
            "receipts ON ReceiptGoodCrossRef.receipt_id = receipts.receipt_id WHERE " +
            "receipts.date_time BETWEEN :dayFrom AND :dayTo AND goods.group_id IN (:groups) " +
            "GROUP BY new_names.name_id " +
            "ORDER BY total DESC")
    suspend fun getListGoodsBetweenDays(groups: LongArray, dayFrom: Long, dayTo: Long): List<GoodsInGroup>


    @Query("SELECT SUM(goods.price * ReceiptGoodCrossRef.quantity) FROM " +
            "new_names JOIN goods ON new_names.name_id = goods.name_id JOIN " +
            "ReceiptGoodCrossRef ON goods.good_id = ReceiptGoodCrossRef.good_id JOIN " +
            "receipts ON ReceiptGoodCrossRef.receipt_id = receipts.receipt_id WHERE " +
            "new_names.name_id = :nameId AND receipts.date_time BETWEEN :dayFrom AND :dayTo")
    suspend fun getGoodCostBetweenDays(nameId: Long, dayFrom: Long, dayTo: Long): Double?

    @Query("SELECT group_id FROM goods WHERE name_id = :nameId")
    suspend fun getGroupIdByGoodName(nameId: Long): Long?

    @Query("SELECT suffix FROM goods WHERE name_id = :nameId")
    suspend fun getUnitByNameId(nameId: Long): String

    @Query("UPDATE goods SET group_id = :groupId, name_id = :nameId, suffix = :suffix WHERE good_id = :goodId")
    suspend fun updateGood(goodId: Long, groupId: Long, nameId: Long, suffix: String)

    @Query("SELECT COUNT(*) FROM goods WHERE name_id = :nameId")
    suspend fun countNameUses(nameId: Long): Int
}