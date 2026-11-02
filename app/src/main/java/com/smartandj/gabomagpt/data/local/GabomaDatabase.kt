package com.smartandj.gabomagpt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartandj.gabomagpt.data.local.dao.ChatDao
import com.smartandj.gabomagpt.data.local.entity.ConversationEntity
import com.smartandj.gabomagpt.data.local.entity.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GabomaDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
}
