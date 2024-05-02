package mne.seva.mnereceipt.data.storage

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import mne.seva.mnereceipt.data.storage.dao.GoodDao
import mne.seva.mnereceipt.data.storage.dao.GroupDao
import mne.seva.mnereceipt.data.storage.dao.NewNameDao
import mne.seva.mnereceipt.data.storage.dao.ReceiptDao
import mne.seva.mnereceipt.data.storage.dao.ReceiptGoodCrossRefDao
import mne.seva.mnereceipt.data.storage.dao.ShopDao
import mne.seva.mnereceipt.data.storage.entities.Good
import mne.seva.mnereceipt.data.storage.entities.GoodsInGroup
import mne.seva.mnereceipt.data.storage.entities.GoodWithQuantity
import mne.seva.mnereceipt.data.storage.entities.Group
import mne.seva.mnereceipt.data.storage.entities.NewName
import mne.seva.mnereceipt.data.storage.entities.Receipt
import mne.seva.mnereceipt.data.storage.entities.ReceiptGoodCrossRef
import mne.seva.mnereceipt.data.storage.entities.ReceiptWithShop
import mne.seva.mnereceipt.data.storage.entities.Shop
import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.GroupCost
import mne.seva.mnereceipt.domain.models.NameWithId
import mne.seva.mnereceipt.domain.models.NameWithPriceId
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.domain.models.ShopWithPrice


class DbReceiptStorage(private val shopDao: ShopDao,
                       private val groupDao: GroupDao,
                       private val newNameDao: NewNameDao,
                       private val receiptDao: ReceiptDao,
                       private val goodDao: GoodDao,
                       private val receiptGoodCrossRefDao: ReceiptGoodCrossRefDao
) : ReceiptStorage {

    @WorkerThread
    override suspend fun insertReceipt(time: Long, downloadedReceipt: DownloadedReceipt): Long {
        return receiptDao.insert(Receipt(receiptId = 0, iic = downloadedReceipt.receiptNumber.iic,
        tin = downloadedReceipt.receiptNumber.tin, dateTimeUtc = time,
        total = downloadedReceipt.totalPrice, byCash = downloadedReceipt.byCash,
        shopId = downloadedReceipt.shopId, currencyType = downloadedReceipt.currencyType,
        country = downloadedReceipt.country))
    }

    @WorkerThread
    suspend fun isReceiptExist(receiptNumber: ReceiptNumber) : Boolean {
        return receiptDao.isReceiptExists(iic = receiptNumber.iic, tin = receiptNumber.tin)
    }

    @WorkerThread
    suspend fun getReceiptById(receiptId: Long): ReceiptWithShop {
        return receiptDao.getReceiptById(receiptId = receiptId)
    }

    @WorkerThread
    suspend fun insertNewShop(origShopName: String, newShopName: String): Long {
        return shopDao.insert(Shop(shop_id = 0, name_orig = origShopName, short_name = newShopName))
    }

    @WorkerThread
    suspend fun isShopExists(name: String): Boolean {
        return shopDao.isShopNameExists(name)
    }

    @WorkerThread
    suspend fun getShopId(origShopName: String): Long? {
        return shopDao.getShopId(origShopName)
    }

    @WorkerThread
    suspend fun getShopName(id: Long): String {
        return shopDao.getShopName(id)
    }

    @WorkerThread
    suspend fun getShopIdByShortName(newShopName: String): Long? {
        return shopDao.getShopIdByShortName(newShopName)
    }

    @WorkerThread
    suspend fun updateShop(shopId: Long, newName: String) {
        shopDao.updateShop(shopId = shopId, newName = newName)
    }

    @WorkerThread
    fun getAllShops(): Flow<List<mne.seva.mnereceipt.domain.models.Shop>> {
        return shopDao.getAllShops()
    }

    @WorkerThread
    fun getAllReceiptsWithShop(): LiveData<List<ReceiptWithShop>> {
        return receiptDao.getAllReceipts()
    }

    @WorkerThread
    suspend fun insertNewGroup(group: Group) {
        groupDao.insert(group)
    }

    @WorkerThread
    fun getAllGroups(): Flow<List<mne.seva.mnereceipt.domain.models.Group>> {
        return groupDao.getAllGroup()
    }

    @WorkerThread
    suspend fun getGoodGroupId(name: String): Long? {
        return groupDao.getGroupId(name)
    }

    @WorkerThread
    suspend fun renameGroupById(id: Long, newName: String) {
        groupDao.renameGroupById(id = id, newName = newName)
    }

    @WorkerThread
    suspend fun getReceiptGoodList(receiptId: Long): List<GoodWithQuantity> {
        return goodDao.getReceiptGoodList(receiptId)
    }

    @WorkerThread
    suspend fun insertNewName(newName: String): Long {
        return newNameDao.insert(NewName(name_id = 0, name = newName))
    }

    @WorkerThread
    suspend fun getNameId(name: String): Long {
        return newNameDao.getNameId(name) ?: -1L
    }

    @WorkerThread
    suspend fun updateName(nameId: Long, newName: String) {
        newNameDao.updateName(nameId, newName)
    }

    @WorkerThread
    suspend fun getGoodNewName(goodId: Long): String {
        return newNameDao.getGoodNewName(goodId)
    }

    @WorkerThread
    fun getAllNames(): Flow<List<String>> {
        return newNameDao.getAllNames()
    }

    @WorkerThread
    fun getAllNamesWithId(): LiveData<List<NameWithId>> {
        return newNameDao.getAllNamesWithId()
    }

    @WorkerThread
    suspend fun countNameUses(nameId: Long): Int {
        return goodDao.countNameUses(nameId = nameId)
    }

    @WorkerThread
    suspend fun deleteNameById(nameId: Long) {
        newNameDao.deleteNameById(name = NewName(name_id = nameId, name = ""))
    }

    @WorkerThread
    suspend fun insertGood(shopId: Long, good: NewGood): Long {
        return goodDao.insert(Good(goodId = good.goodId, nameOrig = good.nameOrig,
        price = good.price, groupId = good.groupId, nameId = good.newNameId,
        shopId = shopId, suffix = good.suffix, currencyType = good.currencyType, code = good.code,
        country = good.country))
    }

    @WorkerThread
    suspend fun getGoodById(goodId: Long): Good {
        return goodDao.getGoodById(goodId)
    }

    @WorkerThread
    suspend fun getListGoodsBetweenDays(arrayGroups: LongArray, dayFrom: Long, dayTo: Long): List<GoodsInGroup> {
        return goodDao.getListGoodsBetweenDays(arrayGroups, dayFrom, dayTo)
    }

    @WorkerThread
    suspend fun getExactGoodId(origName: String, code: String?, shopId: Long, price: Double): Long {

        var id = 0L
        if (code != null) {
            id = try {
                goodDao.getExactGoodIdByCode(code = code, shopId = shopId, currPrice = price) ?: 0L
            } catch (e: NullPointerException) {
                0L
            }
        }

        if (id == 0L) {
            id = try {
                goodDao.getExactGoodIdByOrigName(origName = origName, shopId = shopId, currPrice = price) ?: 0L
            } catch (e: NullPointerException) {
                0L
            }
        }

        return id
    }

    @WorkerThread
    suspend fun getProbGoodId(code: String?, origName: String): Long {
        var id = 0L
        if (code != null) {
            id = goodDao.getProbGoodIdByCode(code = code) ?: 0L
        }

        if (id == 0L) {
            id = goodDao.getProbGoodIdByOrigName(origName = origName) ?: 0L
        }

        return id
    }

    @WorkerThread
    suspend fun insertCrossRef(receiptGoodCrossRef: ReceiptGoodCrossRef) {
        receiptGoodCrossRefDao.insert(receiptGoodCrossRef)
    }

    @WorkerThread
    suspend fun getCostBetweenDays(groupsIds: LongArray, fromDay: Long, toDay: Long): Double {
        return goodDao.getCostBetweenDays(
            groups = groupsIds,
            unixTimeFrom = fromDay,
            unixTimeTo = toDay
        ) ?: 0.0
    }

    @WorkerThread
    suspend fun getCostsListByDates(): List<CostByPeriod> {
        return receiptDao.getCostsListByDates()
    }

    @WorkerThread
    suspend fun getCostsListByWeeks(): List<CostByPeriod> {
        return receiptDao.getCostsListByWeeks()
    }

    @WorkerThread
    suspend fun getCostsListByMonths():List<CostByPeriod> {
        return receiptDao.getCostsListByMonths()
    }

    @WorkerThread
    suspend fun getOrigNamesById(id: Long): List<NameWithPriceId> {
        return newNameDao.getOrigNamesById(id)
    }

    @WorkerThread
    suspend fun getGroupNameByNameId(id: Long): String {
        return groupDao.getGroupNameByNameId(nameId = id)
    }

    @WorkerThread
    suspend fun getGoodCostBetweenDays(nameId: Long, dayFrom: Long, dayTo: Long): Double {
        return goodDao.getGoodCostBetweenDays(nameId, dayFrom, dayTo) ?: 0.0
    }

    @WorkerThread
    suspend fun getListShopsPrice(nameId: Long): List<ShopWithPrice> {
        return shopDao.getListShopsPrice(nameId)
    }

    @WorkerThread
    suspend fun getGroupIdByGoodName(nameId: Long): Long {
        return goodDao.getGroupIdByGoodName(nameId) ?: 1L
    }

    @WorkerThread
    suspend fun getAllGroupsList(): List<mne.seva.mnereceipt.domain.models.Group> {
        return groupDao.getAllGroupsList()
    }

    @WorkerThread
    suspend fun getUnitByNameId(nameId: Long): String {
        return goodDao.getUnitByNameId(nameId)
    }

    @WorkerThread
    suspend fun updateGood(goodId: Long, groupId: Long, nameId: Long, suffix: String) {
        goodDao.updateGood(goodId = goodId, groupId = groupId, nameId = nameId, suffix = suffix)
    }

    @WorkerThread
    suspend fun getGroupCostListFromTo(dayFrom: Long, dayTo: Long): List<GroupCost> {
        return groupDao.getGroupCostListFromTo(dayFrom = dayFrom, dayTo = dayTo)
    }


}