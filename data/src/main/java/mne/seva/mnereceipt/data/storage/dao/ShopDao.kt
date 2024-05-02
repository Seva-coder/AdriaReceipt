package mne.seva.mnereceipt.data.storage.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mne.seva.mnereceipt.data.storage.entities.Shop
import mne.seva.mnereceipt.domain.models.ShopWithPrice

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(shop: Shop): Long

    @Query("SELECT EXISTS (SELECT * FROM shops WHERE short_name = :name)")
    suspend fun isShopNameExists(name: String): Boolean

    @Query("SELECT shop_id FROM shops WHERE name_orig = :origShopName")
    suspend fun getShopId(origShopName: String): Long?

    @Query("SELECT shop_id FROM shops WHERE short_name = :newShopName")
    suspend fun getShopIdByShortName(newShopName: String): Long?

    @Query("SELECT short_name FROM shops WHERE shop_id = :id")
    suspend fun getShopName(id: Long): String

    @Query("SELECT shops.short_name AS name, goods.price AS price, goods.suffix AS suffix, MAX(receipts.date_time) AS date " +
            "FROM shops JOIN goods ON shops.shop_id = goods.shop_id " +
            "JOIN ReceiptGoodCrossRef ON goods.good_id = ReceiptGoodCrossRef.good_id " +
            "JOIN receipts ON ReceiptGoodCrossRef.receipt_id = receipts.receipt_id " +
            "WHERE goods.name_id = :goodNameId GROUP BY shops.short_name ORDER BY goods.price ASC")
    suspend fun getListShopsPrice(goodNameId: Long): List<ShopWithPrice>

    @Query("SELECT shop_id AS id, name_orig AS origName, short_name AS newName FROM shops")
    fun getAllShops(): Flow<List<mne.seva.mnereceipt.domain.models.Shop>>

    @Query("UPDATE shops SET short_name = :newName WHERE shop_id = :shopId")
    suspend fun updateShop(shopId: Long, newName: String)
}