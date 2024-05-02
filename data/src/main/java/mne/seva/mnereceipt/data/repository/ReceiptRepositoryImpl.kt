package mne.seva.mnereceipt.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import mne.seva.mnereceipt.data.storage.DbReceiptStorage
import mne.seva.mnereceipt.data.storage.entities.GoodsInGroup
import mne.seva.mnereceipt.data.storage.entities.ReceiptGoodCrossRef
import mne.seva.mnereceipt.data.storage.entities.ReceiptWithShop
import mne.seva.mnereceipt.domain.models.CostByPeriod

import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.Group
import mne.seva.mnereceipt.domain.models.GroupCost
import mne.seva.mnereceipt.domain.models.NameWithId
import mne.seva.mnereceipt.domain.models.NameWithPriceId
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.models.Shop
import mne.seva.mnereceipt.domain.models.ShopWithPrice
import mne.seva.mnereceipt.domain.repository.ReceiptRepository



class ReceiptRepositoryImpl(private val receiptStorage: DbReceiptStorage, private val network: NetworkRequests) : ReceiptRepository {

    val listGroups: Flow<List<Group>> = receiptStorage.getAllGroups()
    val listNames: Flow<List<String>> = receiptStorage.getAllNames()
    val listReceipts: LiveData<List<ReceiptWithShop>> by lazy { receiptStorage.getAllReceiptsWithShop() }
    val listAllGoods: LiveData<List<NameWithId>> by lazy { receiptStorage.getAllNamesWithId() }
    val listShops: Flow<List<Shop>> by lazy { receiptStorage.getAllShops() }

    override suspend fun downloadMNEreceipt(number: ReceiptNumber, defaultName: Boolean,
                                            defaultGroup: Boolean, defaultUnit: Boolean): DownloadedReceipt {
        return downloadMNE(receiptStorage = receiptStorage, network = network, number = number,
            defaultName = defaultName, defaultGroup = defaultGroup, defaultUnit = defaultUnit)
    }

    override fun stopDownload() {
        network.stopAllRequests()
    }

    override suspend fun isReceiptExist(number: ReceiptNumber): Boolean {
        return receiptStorage.isReceiptExist(number)
    }

    override suspend fun getGroupId(name: String): Long {
        val a = receiptStorage.getGoodGroupId(name)
        return a ?: -1L
    }

    override suspend fun insertGroup(name: String): Long {
        receiptStorage.insertNewGroup(
            mne.seva.mnereceipt.data.storage.entities.Group(group_id = 0L, name = name)
        )
        return getGroupId(name)
    }

    override suspend fun isShopExist(newShopName: String): Boolean {
        return receiptStorage.isShopExists(newShopName)
    }

    @Transaction
    override suspend fun saveReceipt(newShopName: String, downloadedReceipt: DownloadedReceipt) {
        if (downloadedReceipt.shopId == 0L) {
            downloadedReceipt.shopId = receiptStorage.insertNewShop(
                origShopName = downloadedReceipt.shopName, newShopName = newShopName)
        }

        val unixTime = downloadedReceipt.dateTimeUnix

        val receiptId = receiptStorage.insertReceipt(unixTime, downloadedReceipt)

        for (good in downloadedReceipt.listOfGoods) {
            if (good.goodId != 0L) {
                receiptStorage.insertCrossRef(ReceiptGoodCrossRef(
                    goodId = good.goodId, quantity = good.quantity, receiptId = receiptId)
                )
                continue
            }

            // case of new good - need to get all id's and write to CrossRef
            val nameId: Long = receiptStorage.getNameId(good.newName)
            good.newNameId = if (nameId == -1L) receiptStorage.insertNewName(good.newName) else nameId

            good.goodId = receiptStorage.insertGood(shopId = downloadedReceipt.shopId, good = good)

            receiptStorage.insertCrossRef(ReceiptGoodCrossRef(
                goodId = good.goodId, quantity = good.quantity, receiptId = receiptId)
            )
        }
    }

    override suspend fun getReceiptById(receiptId: Long): DownloadedReceipt {
        val receiptWithShop = receiptStorage.getReceiptById(receiptId)
        val goodList = receiptStorage.getReceiptGoodList(receiptId)
        val goodListNewGood: ArrayList<NewGood> = ArrayList()
        goodList.forEach {
            goodListNewGood.add(
                NewGood(
                    goodId = 0,
                    nameOrig = "",
                    newNameSetted = true,
                    newName = it.name,
                    newNameId = 0,
                    quantity = it.quantity,
                    suffix = it.suffix,
                    suffixSetted = true,
                    price = it.price,
                    total = it.price * it.quantity,
                    groupId = 1,
                    groupSetted = true,
                    currencyType = "EUR",
                    code = null,
                    country = "MNE"
                )
            )
        }
        val receiptNumber = ReceiptNumber(tin = "", iic = "", date_time = "")
        val date = receiptWithShop.date

        return DownloadedReceipt(
            shopName = receiptWithShop.shopName,
            shopId = 0,
            dateTimeUnix = date,
            totalPrice = receiptWithShop.total,
            byCash = receiptWithShop.byCash,
            receiptNumber = receiptNumber,
            listOfGoods = goodListNewGood,
            currencyType = receiptWithShop.currencyType,
            country = "MNE"
        )
    }

    override suspend fun getCostBetweenDays(setGroups: Set<Long>, dayFrom: Long, dayTo: Long): Double {
        return receiptStorage.getCostBetweenDays(groupsIds = setGroups.toLongArray(), fromDay = dayFrom, toDay = dayTo)
    }

    suspend fun getListGoodsBetweenDays(set: Set<Long>, dayFrom: Long, dayTo: Long): List<GoodsInGroup> {
        return receiptStorage.getListGoodsBetweenDays(set.toLongArray(), dayFrom = dayFrom, dayTo = dayTo)
    }

    override suspend fun getOrigNamesById(id: Long): List<NameWithPriceId> {
        return receiptStorage.getOrigNamesById(id)
    }

    override suspend fun getGroup(nameId: Long): String {
        return receiptStorage.getGroupNameByNameId(nameId)
    }

    override suspend fun getGoodCostBetweenDays(nameId: Long, dayFrom: Long, dayTo: Long): Double {
        return receiptStorage.getGoodCostBetweenDays(nameId, dayFrom, dayTo)
    }

    override suspend fun getListShopsPrice(goodNameId: Long): List<ShopWithPrice> {
        return receiptStorage.getListShopsPrice(goodNameId)
    }

    override suspend fun getGroupIdByGoodName(nameId: Long): Long {
        return receiptStorage.getGroupIdByGoodName(nameId)
    }

    override suspend fun getAllGroups(): List<Group> {
        return receiptStorage.getAllGroupsList()
    }

    override suspend fun getUnitByNameId(id: Long): String {
        return receiptStorage.getUnitByNameId(id)
    }

    override suspend fun getNewNameId(newName: String): Long {
        return receiptStorage.getNameId(newName)
    }

    override suspend fun insertNewName(newName: String): Long {
        return receiptStorage.insertNewName(newName)
    }

    override suspend fun updateGood(goodId: Long, groupId: Long, nameId: Long, suffix: String) {
        receiptStorage.updateGood(goodId = goodId, groupId = groupId, nameId = nameId, suffix = suffix)
    }

    override suspend fun updateName(nameId: Long, newName: String) {
        receiptStorage.updateName(nameId = nameId, newName = newName)
    }

    override suspend fun countNameUses(nameId: Long): Int {
        return receiptStorage.countNameUses(nameId = nameId)
    }

    override suspend fun deleteNameById(nameId: Long) {
        receiptStorage.deleteNameById(nameId = nameId)
    }

    override suspend fun updateShop(shopId: Long, newName: String) {
        receiptStorage.updateShop(shopId = shopId, newName = newName)
    }

    override suspend fun getShopIdByShortName(newShopName: String): Long? {
        return receiptStorage.getShopIdByShortName(newShopName)
    }

    override suspend fun getGroupCostListFromTo(dayFrom: Long, dayTo: Long): List<GroupCost> {
        return receiptStorage.getGroupCostListFromTo(dayFrom, dayTo)
    }

    override suspend fun renameGroupById(id: Long, newName: String) {
        receiptStorage.renameGroupById(id = id, newName = newName)
    }

    override suspend fun getCostsListByDates(): List<CostByPeriod> {
        return receiptStorage.getCostsListByDates()
    }

    override suspend fun getCostsListByWeeks(): List<CostByPeriod> {
        return receiptStorage.getCostsListByWeeks()
    }

    override suspend fun getCostsListByMonths(): List<CostByPeriod> {
        return receiptStorage.getCostsListByMonths()
    }


}