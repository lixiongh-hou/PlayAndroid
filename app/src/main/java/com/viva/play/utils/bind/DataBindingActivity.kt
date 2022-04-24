package com.viva.play.utils.bind

import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

inline fun <reified T : ViewBinding> AppCompatActivity.binding() =
    ActivityDataBindingDelegate(T::class.java, this)