package com.viva.play.adapter.vp2

import android.graphics.Typeface
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.viva.play.R
import com.viva.play.utils.getThemeColor
import com.viva.play.views.AnimationRadioView

/**
 * @author 李雄厚
 *
 *
 */
class MainTabAdapter : TabFragmentPagerAdapter.Page.TabAdapter<TabEntity> {
    override fun onBindData(view: View, data: TabEntity, selected: Boolean) {
        val ivTabIcon = view.findViewById<AnimationRadioView>(R.id.ivTabIcon)
        val tvTabName = view.findViewById<AppCompatTextView>(R.id.tvTabName)
        if (data.isNight) {
            ivTabIcon.setAnimation(data.tabIconNight)
        } else {
            ivTabIcon.setAnimation(data.tabIcon)
        }
        ivTabIcon.isChecked = selected
        tvTabName.text = data.tabName
        tvTabName.setTextColor(
            if (selected)
                view.getThemeColor(R.attr.colorTextMain)
            else
                view.getThemeColor(R.attr.colorTextSurface)
        )
        tvTabName.typeface = if (selected)
            Typeface.DEFAULT_BOLD
        else
            Typeface.DEFAULT
    }

    override fun onDoubleTap(fragment: Fragment) {
    }
}