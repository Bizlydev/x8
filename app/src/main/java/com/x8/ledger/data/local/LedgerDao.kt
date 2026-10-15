package com.x8.ledger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    @Query("SELECT * FROM customers ORDER BY name COLLATE NOCASE ASC")
    fun observeCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    suspend fun getCustomer(customerId: Long): CustomerEntity?

    @Insert
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Query("SELECT * FROM ledger_entries WHERE customerId = :customerId ORDER BY createdAt DESC, id DESC")
    fun observeEntries(customerId: Long): Flow<List<LedgerEntryEntity>>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amountToman WHEN type = 'PAYMENT' THEN -amountToman ELSE 0 END), 0) " +
            "FROM ledger_entries WHERE customerId = :customerId"
    )
    fun observeBalance(customerId: Long): Flow<Long>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amountToman WHEN type = 'PAYMENT' THEN -amountToman ELSE 0 END), 0) " +
            "FROM ledger_entries WHERE customerId = :customerId"
    )
    suspend fun getBalance(customerId: Long): Long

    @Insert
    suspend fun insertEntry(entry: LedgerEntryEntity): Long
}
