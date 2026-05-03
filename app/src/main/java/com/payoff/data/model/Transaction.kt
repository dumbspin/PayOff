package com.payoff.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class TransactionDirection { SENT, RECEIVED }

enum class TransactionStatus { SUCCESS, FAILED, PENDING, UNKNOWN }

/**
 * Immutable log entry written after every USSD session (FR-08).
 * Amount and ussdCharge are stored in paise (₹1 = 100 paise) to avoid float precision issues.
 * PIN is NEVER stored here. (SEC-01)
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val contactId: String? = null,
    val recipientLabel: String,
    val recipientUpi: String,
    val amountPaise: Long,                         // e.g. 50000 = ₹500.00
    val direction: TransactionDirection,
    val status: TransactionStatus,
    val ussdRef: String? = null,
    val accountId: String,
    val ussdChargePaise: Long = 50L,               // ₹0.50 = 50 paise
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null
) {
    val amountRupees: Double get() = amountPaise / 100.0
    val ussdChargeRupees: Double get() = ussdChargePaise / 100.0
    val totalDeductedRupees: Double get() = (amountPaise + ussdChargePaise) / 100.0
}
