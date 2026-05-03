package com.payoff.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Represents a saved payment contact (FR-07).
 * Stores UPI ID, mobile, or bank account+IFSC — whichever the user provides.
 * avatarColor is a Material color token index for the circular avatar.
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val upiId: String? = null,
    val mobile: String? = null,
    val accountNumber: String? = null,
    val ifsc: String? = null,
    val avatarColor: Int = 0,
    val isFavourite: Boolean = false,
    val lastUsed: Long = 0L
) {
    /** Returns the best display identifier for this contact in the UI */
    val displayId: String
        get() = upiId ?: mobile ?: if (accountNumber != null && ifsc != null) "$accountNumber • $ifsc" else ""
}
