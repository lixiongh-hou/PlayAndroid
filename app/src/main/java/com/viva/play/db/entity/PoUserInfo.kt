package com.viva.play.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.viva.play.db.converters.UserInfoConverters
import com.viva.play.service.entity.UserInfo

/**
 * @author 李雄厚
 *
 *个人信息
 */
@Entity(tableName = "UserInfo")
@TypeConverters(value = [UserInfoConverters::class])
data class PoUserInfo(
    val admin: Boolean,
    val coinCount: Int,
    val collectIds: List<Int>,
    val email: String,
    val icon: String,
    @PrimaryKey
    val id: Int,
    val nickname: String,
    val password: String,
    val publicName: String,
    val token: String,
    val type: Int,
    val username: String,
    val rank: Int = -1
) {
    companion object {
        fun parse(userInfo: UserInfo): PoUserInfo {
            return PoUserInfo(
                userInfo.admin,
                userInfo.coinCount,
                userInfo.collectIds,
                userInfo.email,
                userInfo.icon,
                userInfo.id,
                userInfo.nickname,
                userInfo.password,
                userInfo.publicName,
                userInfo.token,
                userInfo.type,
                userInfo.username,
                -1
            )
        }
    }
}