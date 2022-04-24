package com.viva.play.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.viva.play.db.entity.PoUserInfo

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface PoUserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: PoUserInfo)

    @Query("SELECT * FROM UserInfo WHERE id = :id")
    fun findUserInfo(id: Int): PoUserInfo?

    @Query("UPDATE UserInfo SET rank = :rank WHERE id = :id")
    fun updateRank(id: Int, rank: Int)

    @Query("DELETE FROM UserInfo")
    fun delete()
}