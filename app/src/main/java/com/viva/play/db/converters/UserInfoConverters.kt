package com.viva.play.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author 李雄厚
 *
 *
 */
class UserInfoConverters {

    @TypeConverter
    fun listToString(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToList(value: String): List<Int> {
        return Gson().fromJson(value, object : TypeToken<List<Int>>() {}.type)
    }
}