package mne.seva.mnereceipt.data.repository

import mne.seva.mnereceipt.data.storage.DbReceiptStorage
import mne.seva.mnereceipt.domain.models.DownloadedReceipt
import mne.seva.mnereceipt.domain.models.NewGood
import mne.seva.mnereceipt.domain.models.ReceiptNumber
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

suspend fun downloadMNE(receiptStorage: DbReceiptStorage,
                        network: NetworkRequests,
                        number: ReceiptNumber,
                        defaultName: Boolean,
                        defaultGroup: Boolean,
                        defaultUnit: Boolean): DownloadedReceipt {

    val jsonResult = network.downloadReceiptJSON(number = number)

    //exceptions will be handled in ViewModel
    val receiptJson = JSONObject(jsonResult)
    var shopName = receiptJson.getJSONObject("seller").getString("name")
    var dateStr = receiptJson.getString("dateTimeCreated")
    val cashOrCoins = receiptJson.getJSONArray("paymentMethod")
        .getJSONObject(0).getString("typeCode")
    val byCash = (cashOrCoins != "CARD")

    val totalPrice = receiptJson.getDouble("totalPrice")

    val receiptShopId: Long = receiptStorage.getShopId(shopName) ?: 0L

    if (receiptShopId != 0L) {
        shopName = receiptStorage.getShopName(receiptShopId)
    }

    val goodMap = mutableMapOf<String, NewGood>()

    val items = receiptJson.getJSONArray("items")
    for (index in 0 until items.length()) {
        val goodJson: JSONObject = items.getJSONObject(index)

        val nameOrig = goodJson.getString("name")
        val quantity: Double = goodJson.getDouble("quantity")
        val price: Double = goodJson.getDouble("unitPriceAfterVat")
        val total: Double = goodJson.getDouble("priceAfterVat")

        val code: String? = if (goodJson.isNull("code")) {
            null
        } else {
            try {
                goodJson.getString("code")
            } catch (e: JSONException) {
                null
            }
        }

        var newName = ""
        var newNameSetted = false

        if (defaultName) {
            newName = nameOrig
            newNameSetted = true
        }

        var suffix = ""
        var suffixSetted = false

        if (defaultUnit) {
            suffix = "kg"
            suffixSetted = true
        }

        var groupId = 0L
        var groupSetted = false

        if (defaultGroup) {
            // 1 - "Products" id
            groupId = 1L // "If the table has never before contained any data, then a ROWID of 1 is used" https://www.sqlite.org/autoinc.html
            groupSetted = true
        }

        var goodId = 0L
        var exactGoodId = 0L
        if (receiptShopId != 0L) {
            exactGoodId = receiptStorage.getExactGoodId(origName = nameOrig, code = code, shopId = receiptShopId, price = price)
        }

        if (exactGoodId != 0L) {  // good already exist - mark it green in list
            val good = receiptStorage.getGoodById(exactGoodId)
            newName = receiptStorage.getGoodNewName(goodId = exactGoodId)
            newNameSetted = true
            suffix = good.suffix
            suffixSetted = true
            groupId = good.groupId
            groupSetted = true
            goodId = exactGoodId
        } else {
            val probGoodId = receiptStorage.getProbGoodId(origName = nameOrig, code = code)
            if (probGoodId != 0L) {
                val good = receiptStorage.getGoodById(probGoodId)
                newName = receiptStorage.getGoodNewName(goodId = probGoodId)
                newNameSetted = true
                suffix = good.suffix
                suffixSetted = true
                groupId = good.groupId
                groupSetted = true
            } // goodId=0L already
        }

        val good = NewGood(
            goodId = goodId, nameOrig = nameOrig, newName = newName,
            newNameSetted = newNameSetted, quantity = quantity, suffix = suffix,
            suffixSetted = suffixSetted, price = price, total = total, groupId = groupId,
            groupSetted = groupSetted, newNameId = -1, currencyType = "EUR", code = code,
            country = "MNE"
        )

        // add quantity if good already was in receipt
        if (nameOrig in goodMap) {
            goodMap[nameOrig]!!.quantity = goodMap[nameOrig]!!.quantity + quantity
            goodMap[nameOrig]!!.total = goodMap[nameOrig]!!.quantity * goodMap[nameOrig]!!.price
        } else {
            goodMap[nameOrig] = good
        }
    }

    dateStr = dateStr.substring(0, 20)
    val date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val unixTime = date.toEpochSecond(ZoneOffset.UTC)

    return DownloadedReceipt(
        shopName = shopName,
        shopId = receiptShopId,
        dateTimeUnix = unixTime,
        totalPrice = totalPrice,
        byCash = byCash,
        receiptNumber = number,
        listOfGoods = goodMap.values.toList(),
        currencyType = "EUR",
        country = "MNE"
    )

}