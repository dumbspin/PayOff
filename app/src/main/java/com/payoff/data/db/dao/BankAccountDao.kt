package com.payoff.data.db.dao

import androidx.room.*
import com.payoff.data.model.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Query("SELECT * FROM bank_accounts ORDER BY isDefault DESC")
    fun observeAll(): Flow<List<BankAccount>>

    @Query("SELECT * FROM bank_accounts WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): BankAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: BankAccount)

    @Delete
    suspend fun delete(account: BankAccount)

    @Transaction
    suspend fun setDefault(accountId: String) {
        clearDefault()
        markDefault(accountId)
    }

    @Query("UPDATE bank_accounts SET isDefault = 0")
    suspend fun clearDefault()

    @Query("UPDATE bank_accounts SET isDefault = 1 WHERE id = :id")
    suspend fun markDefault(id: String)

    @Query("SELECT COUNT(*) FROM bank_accounts")
    suspend fun count(): Int
}
