package com.viva.play.utils

import android.widget.ImageView
import coil.load

/**
 * @author 李雄厚
 *
 *
 */

fun ImageView.banner(url: String) {
    this.load(url)
}