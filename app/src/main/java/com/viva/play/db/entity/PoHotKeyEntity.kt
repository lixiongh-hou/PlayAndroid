package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author 李雄厚
 *
 *
 */
@Entity(tableName = "HotKey")
data class PoHotKeyEntity(
    @PrimaryKey
    val key: String,
)