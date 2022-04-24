package com.viva.play.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.viva.play.App
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.reflect.KProperty

/**
 * @author 李雄厚
 *
 * @features 公共SP文件存储，通过委托方式来进行存储和查询
 * 使用方法:
 * 1.定义全局变量   private var rsa by SharedPref(Constant.RSA, "")
 * 2.存储数据  rsa = "我存储的数据"
 * 3.取出数据 Toast.show(rsa)
 */
@Suppress("UNCHECKED_CAST")
class Preference<T>(){
    private var keyName: String? = null
    private var defaultValue: T? = null

    constructor(keyName: String,defaultValue: T) : this() {
        this.keyName = keyName
        this.defaultValue = defaultValue
    }

    private val prefs: SharedPreferences by lazy {
        App.instance.applicationContext.getSharedPreferences("Preference", Context.MODE_PRIVATE)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T  {
        return findSharedPreference(keyName!!, defaultValue!!)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(keyName!!, value)
    }


    /**
     * 查找数据 返回一个具体的对象
     * 没有查找到value 就返回默认的序列化对象，然后经过反序列化返回
     */
    @Suppress("UNCHECKED_CAST")
    private fun  findSharedPreference(name: String, default: T): T = with(prefs) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default)))
        }
        res as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun  putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }


    /**
     * 删除全部数据
     */
    fun clear() {
        prefs.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * 序列化对象
     */
    @Throws(Exception::class)
    private fun <T> serialize(obj: T): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     */
    @Throws(Exception::class)
    private fun <T> deSerialization(str: String?): T {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream)
        val obj = objectInputStream.readObject() as T
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


}