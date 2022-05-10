package com.viva.play.db.dao

import androidx.room.*
import com.viva.play.db.entity.PoChapterChildrenEntity
import com.viva.play.db.entity.PoChapterEntity
import com.viva.play.db.entity.Status
import com.viva.play.ui.vo.VoChapterEntity

/**
 * @author 李雄厚
 *
 *
 */
@Dao
interface ChapterDao {

    @Insert
    fun insert(data: List<PoChapterEntity>)

    @Insert
    fun insertChildren(data: List<PoChapterChildrenEntity>)

    @Query("SELECT COUNT(id) AS total FROM Chapter WHERE status=:status")
    suspend fun totalKnowledge(status: Status = Status.KNOWLEDGE): Int

    @Transaction
    @Query("SELECT * FROM Chapter WHERE status=:status ORDER BY `index`")
    fun findKnowledge(status: Status = Status.KNOWLEDGE): List<VoChapterEntity>

    @Query("SELECT COUNT(id) FROM Chapter WHERE status=:status")
    suspend fun totalNavi(status: Status = Status.NAVI): Int

    @Transaction
    @Query("SELECT * FROM Chapter WHERE status=:status ORDER BY `index`")
    fun findNavi(status: Status = Status.NAVI): List<VoChapterEntity>

}
