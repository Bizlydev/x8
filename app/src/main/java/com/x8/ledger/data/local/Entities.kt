package com.x8.ledger.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [Index(value = ["name"])]
)
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["customerId", "createdAt"])]
)
data class LedgerEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    /** CREDIT means the customer bought on credit; PAYMENT means money received. */
    val type: String,
    /** Always a positive integer number of tomans; floating-point money is forbidden. */
    val amountToman: Long,
    val note: String = "",
    /** UUID generated before each local post, useful for later sync idempotency. */
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CREDIT = "CREDIT"
        const val PAYMENT = "PAYMENT"
    }
}
