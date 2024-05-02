package mne.seva.mnereceipt.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mne.seva.mnereceipt.data.storage.dao.GoodDao
import mne.seva.mnereceipt.data.storage.dao.GroupDao
import mne.seva.mnereceipt.data.storage.dao.NewNameDao
import mne.seva.mnereceipt.data.storage.dao.ReceiptDao
import mne.seva.mnereceipt.data.storage.dao.ReceiptGoodCrossRefDao
import mne.seva.mnereceipt.data.storage.dao.ShopDao
import mne.seva.mnereceipt.data.storage.entities.Good
import mne.seva.mnereceipt.data.storage.entities.Group
import mne.seva.mnereceipt.data.storage.entities.NewName
import mne.seva.mnereceipt.data.storage.entities.Receipt
import mne.seva.mnereceipt.data.storage.entities.ReceiptGoodCrossRef
import mne.seva.mnereceipt.data.storage.entities.Shop

// Annotates class to be a Room Database with a table (entity) of the Shop class
@Database(entities = [Shop::class, Group::class, NewName::class, Receipt::class, Good::class, ReceiptGoodCrossRef::class], version = 1, exportSchema = false)
    abstract class ReceiptDatabase : RoomDatabase() {

    abstract fun shopDao(): ShopDao
    abstract fun groupDao(): GroupDao
    abstract fun newNameDao(): NewNameDao
    abstract fun receiptDao(): ReceiptDao
    abstract fun goodDao(): GoodDao
    abstract fun receiptGoodCrossRefDao(): ReceiptGoodCrossRefDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ReceiptDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ReceiptDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptDatabase::class.java,
                    "receipt_database"
                )
                    //.createFromAsset("receipt_database")  // to create db with my data
                    .addCallback(ReceiptDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }


        private class ReceiptDatabaseCallback(private val scope: CoroutineScope) : Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.groupDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabase(groupDao: GroupDao) {
            //adding one default group
            val defaultGroup = Group(0, "Products")
            groupDao.insert(defaultGroup)
        }

    }
}
