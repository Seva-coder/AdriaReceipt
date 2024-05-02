package mne.seva.mnereceipt.domain.repository

import mne.seva.mnereceipt.domain.models.CostByPeriod
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.Group
import mne.seva.mnereceipt.domain.models.GroupCost
import mne.seva.mnereceipt.domain.models.NameWithPriceId
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import mne.seva.mnereceipt.domain.models.ShopWithPrice


interface ReceiptRepository {

    suspend fun saveReceipt(newShopName: String, downloadedReceipt: DownloadedReceipt)

    suspend fun downloadMNEreceipt(number: ReceiptNumber, defaultName: Boolean,
                                   defaultGroup: Boolean, defaultUnit: Boolean): DownloadedReceipt

    fun stopDownload()

    suspend fun isReceiptExist(number: ReceiptNumber): Boolean

    suspend fun getGroupId(name: String): Long

    suspend fun insertGroup(name: String): Long

    suspend fun isShopExist(newShopName: String): Boolean

    suspend fun getReceiptById(receiptId: Long): DownloadedReceipt

    suspend fun getCostBetweenDays(setGroups: Set<Long>, dayFrom: Long, dayTo: Long): Double

    suspend fun getOrigNamesById(id: Long): List<NameWithPriceId>

    suspend fun getGroup(nameId: Long): String

    suspend fun getGoodCostBetweenDays(nameId: Long, dayFrom: Long, dayTo: Long): Double

    suspend fun getListShopsPrice(goodNameId: Long): List<ShopWithPrice>

    suspend fun getGroupIdByGoodName(nameId: Long): Long

    suspend fun getAllGroups(): List<Group>

    suspend fun getUnitByNameId(id: Long): String

    suspend fun getNewNameId(newName: String): Long

    suspend fun insertNewName(newName: String): Long

    suspend fun updateGood(goodId: Long, groupId: Long, nameId: Long, suffix: String)

    suspend fun updateName(nameId: Long, newName: String)

    suspend fun countNameUses(nameId: Long): Int

    suspend fun deleteNameById(nameId: Long)

    suspend fun updateShop(shopId: Long, newName: String)

    suspend fun getShopIdByShortName(newShopName: String): Long?

    suspend fun getGroupCostListFromTo(dayFrom: Long, dayTo: Long): List<GroupCost>

    suspend fun renameGroupById(id: Long, newName: String)

    suspend fun getCostsListByDates(): List<CostByPeriod>

    suspend fun getCostsListByWeeks(): List<CostByPeriod>

    suspend fun getCostsListByMonths(): List<CostByPeriod>

}