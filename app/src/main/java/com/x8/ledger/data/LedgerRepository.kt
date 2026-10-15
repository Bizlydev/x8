package com.x8.ledger.data

import androidx.room.withTransaction
import com.x8.ledger.data.local.CustomerEntity
import com.x8.ledger.data.local.LedgerEntryEntity
import com.x8.ledger.data.local.X8Database
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class LedgerRepository(private val database: X8Database) {
    private val dao = database.ledgerDao()

    fun observeCustomers(): Flow<List<CustomerEntity>> = dao.observeCustomers()

    fun observeEntries(customerId: Long): Flow<List<LedgerEntryEntity>> =
        dao.observeEntries(customerId)

    fun observeBalance(customerId: Long): Flow<Long> = dao.observeBalance(customerId)

    suspend fun addCustomer(name: String, phone: String, notes: String): Result<Long> {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return Result.failure(IllegalArgumentException("نام مشتری را وارد کن."))
        if (cleanName.length > 120) return Result.failure(IllegalArgumentException("نام مشتری بیش از حد طولانی است."))
        if (phone.length > 40) return Result.failure(IllegalArgumentException("شماره تماس بیش از حد طولانی است."))
        if (notes.length > 1000) return Result.failure(IllegalArgumentException("یادداشت بیش از حد طولانی است."))

        return runCatching {
            dao.insertCustomer(
                CustomerEntity(
                    name = cleanName,
                    phone = phone.trim(),
                    notes = notes.trim()
                )
            )
        }
    }

    suspend fun postCredit(customerId: Long, amountToman: Long, note: String): Result<Unit> =
        postEntry(customerId, LedgerEntryEntity.CREDIT, amountToman, note)

    suspend fun postPayment(customerId: Long, amountToman: Long, note: String): Result<Unit> =
        postEntry(customerId, LedgerEntryEntity.PAYMENT, amountToman, note)

    private suspend fun postEntry(
        customerId: Long,
        type: String,
        amountToman: Long,
        note: String
    ): Result<Unit> = runCatching {
        require(amountToman > 0L) { "مبلغ باید بیشتر از صفر باشد." }
        require(note.length <= 1000) { "توضیحات بیش از حد طولانی است." }

        database.withTransaction {
            require(dao.getCustomer(customerId) != null) { "مشتری پیدا نشد." }
            val currentBalance = dao.getBalance(customerId)
            if (type == LedgerEntryEntity.PAYMENT) {
                require(currentBalance > 0L) { "این مشتری بدهی قابل پرداخت ندارد." }
                require(amountToman <= currentBalance) {
                    "مبلغ پرداخت از بدهی مشتری بیشتر است. مانده فعلی: $currentBalance تومان"
                }
            }
            dao.insertEntry(
                LedgerEntryEntity(
                    customerId = customerId,
                    type = type,
                    amountToman = amountToman,
                    note = note.trim(),
                    idempotencyKey = UUID.randomUUID().toString()
                )
            )
        }
    }
}
