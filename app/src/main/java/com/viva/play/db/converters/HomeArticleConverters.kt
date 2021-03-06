package com.viva.play.db.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.viva.play.service.entity.Tag
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
class HomeArticleConverters {

    @TypeConverter
    fun listToString(value: List<Tag>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun stringToList(value: String?): List<Tag>? {
        return Gson().fromJson(value, object : TypeToken<List<Tag>>() {}.type)
    }

    @TypeConverter
    fun dateToLong(time: Date?): Long? {
        return time?.time
    }

    @TypeConverter
    fun longToDate(millisSinceEpoch: Long?): Date? {
        if (millisSinceEpoch == null) {
            return null
        }
        return Date(millisSinceEpoch)
    }
}