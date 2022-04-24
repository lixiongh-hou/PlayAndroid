package com.viva.play.ui.model

import androidx.lifecycle.ViewModel

/**
 * @author 李雄厚
 *
 *
 */
class MainModel : ViewModel() {
    /**
     * 把数据ViewModel，防止在切换暗黑主题时页面重构导致数据销毁
     */
    var isDarkTheme: Boolean? = null
}