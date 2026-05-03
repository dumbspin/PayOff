package com.payoff.data.db.dao

import androidx.room.*
import com.payoff.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY isFavourite DESC, lastUsed DESC")
    fun observeAll(): Flow<List<Contact>>

    @Query("""
        SELECT * FROM contacts
        WHERE name LIKE '%' || :q || '%'
           OR upiId LIKE '%' || :q || '%'
           OR mobile LIKE '%' || :q || '%'
        ORDER BY isFavourite DESC, lastUsed DESC
    """)
    fun search(q: String): Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getById(id: String): Contact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Query("UPDATE contacts SET lastUsed = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: String, timestamp: Long)

    @Query("UPDATE contacts SET isFavourite = :fav WHERE id = :id")
    suspend fun setFavourite(id: String, fav: Boolean)
}
