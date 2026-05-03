package com.payoff.data.db.dao

import androidx.room.*
import com.payoff.data.model.Transaction
import com.payoff.data.model.TransactionDirection
import com.payoff.data.model.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions
        WHERE (:direction IS NULL OR direction = :direction)
          AND (:status IS NULL OR status = :status)
          AND (:fromTs = 0 OR timestamp >= :fromTs)
          AND (:toTs = 0 OR timestamp <= :toTs)
        ORDER BY timestamp DESC
    """)
    fun observeFiltered(
        direction: String? = null,
        status: String? = null,
        fromTs: Long = 0L,
        toTs: Long = 0L
    ): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 5")
    fun observeRecent(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    /**
     * Monthly summary query — returns total sent, received and charges for the given month.
     * Uses epoch millis range for the month boundaries.
     */
    @Query("""
        SELECT
            COALESCE(SUM(CASE WHEN direction = 'SENT' AND status = 'SUCCESS' THEN amountPaise ELSE 0 END), 0) AS totalSentPaise,
            COALESCE(SUM(CASE WHEN direction = 'RECEIVED' AND status = 'SUCCESS' THEN amountPaise ELSE 0 END), 0) AS totalReceivedPaise,
            COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN ussdChargePaise ELSE 0 END), 0) AS totalChargesPaise
        FROM transactions
        WHERE timestamp >= :fromTs AND timestamp <= :toTs
    """)
    suspend fun getMonthlySummary(fromTs: Long, toTs: Long): MonthlySummary

    @Query("SELECT COALESCE(SUM(ussdChargePaise), 0) FROM transactions WHERE timestamp >= :fromTs AND timestamp <= :toTs")
    suspend fun getMonthlyChargePaise(fromTs: Long, toTs: Long): Long
}

data class MonthlySummary(
    val totalSentPaise: Long,
    val totalReceivedPaise: Long,
    val totalChargesPaise: Long
)
