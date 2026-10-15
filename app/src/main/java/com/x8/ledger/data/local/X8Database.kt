package com.x8.ledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CustomerEntity::class, LedgerEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class X8Database : RoomDatabase() {
    abstract fun ledgerDao(): LedgerDao

    companion object {
        @Volatile
        private var instance: X8Database? = null

        fun getInstance(context: Context): X8Database =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    X8Database::class.java,
                    "x8-local.db"
                ).build().also { instance = it }
            }
    }
}
