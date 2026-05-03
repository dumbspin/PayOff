package com.payoff.data.db

import androidx.room.TypeConverter
import com.payoff.data.model.TransactionDirection
import com.payoff.data.model.TransactionStatus

class Converters {
    @TypeConverter fun fromDirection(value: String): TransactionDirection =
        TransactionDirection.valueOf(value)
    @TypeConverter fun toDirection(dir: TransactionDirection): String = dir.name

    @TypeConverter fun fromStatus(value: String): TransactionStatus =
        TransactionStatus.valueOf(value)
    @TypeConverter fun toStatus(status: TransactionStatus): String = status.name
}
