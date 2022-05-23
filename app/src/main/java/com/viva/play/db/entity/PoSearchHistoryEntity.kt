package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "SearchHistory")
data class PoSearchHistoryEntity(
    @PrimaryKey
    val key: String,
    val time: Date?,
)