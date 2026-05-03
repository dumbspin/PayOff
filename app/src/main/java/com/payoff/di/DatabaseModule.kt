package com.payoff.di

import android.content.Context
import androidx.room.Room
import com.payoff.data.db.PayOffDatabase
import com.payoff.data.db.dao.BankAccountDao
import com.payoff.data.db.dao.ContactDao
import com.payoff.data.db.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PayOffDatabase {
        // TODO: In a real production app, generate AES-256 enc key via Android Keystore
        val dummyPassphrase = "temp-password-implement-keystore" 
        val factory = SupportFactory(dummyPassphrase.toByteArray())

        return Room.databaseBuilder(
            context,
            PayOffDatabase::class.java,
            "payoff_db"
        )
        .openHelperFactory(factory) // SQLCipher encryption (SEC-06)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // PRD 7.2
        .build()
    }

    @Provides
    fun provideContactDao(db: PayOffDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideTransactionDao(db: PayOffDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideBankAccountDao(db: PayOffDatabase): BankAccountDao = db.bankAccountDao()
}
