package com.payoff.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A linked bank account for the user (FR-02).
 * maskedAccount stores only the last 4 digits (SEC-02).
 * Full account number and debit card details are NEVER persisted.
 * language is the USSD session language code for this account.
 */
@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val bankName: String,
    val ifscPrefix: String,      // First 4 chars for *99# bank selection
    val maskedAccount: String,   // "xxxx xxxx 1234" — last 4 only (SEC-02)
    val isDefault: Boolean = false,
    val languageCode: String = "en" // Used in USSD LANG_SELECT state
)
