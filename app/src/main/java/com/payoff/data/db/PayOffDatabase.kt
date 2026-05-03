package com.payoff.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.payoff.data.db.dao.BankAccountDao
import com.payoff.data.db.dao.ContactDao
import com.payoff.data.db.dao.TransactionDao
import com.payoff.data.model.BankAccount
import com.payoff.data.model.Contact
import com.payoff.data.model.Transaction

@Database(
    entities = [Contact::class, Transaction::class, BankAccount::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PayOffDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
}
