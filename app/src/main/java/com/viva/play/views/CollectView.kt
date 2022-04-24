package com.viva.play.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.viva.play.R
import com.viva.play.utils.CookieCache
import per.goweii.heartview.HeartView
import per.goweii.reveallayout.RevealLayout

/**
 * @author 李雄厚
 *
 *
 */
class CollectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RevealLayout(context, attrs) {

    var onClick: ((CollectView) -> Unit)? = null
    private var mUncheckedColor = 0

    init {
        setOnClickListener {
            //这里需要判断一下是否用户登录
            if (CookieCache.doIfLogin(it.context)) {
                onClick?.invoke(this)
            } else {
                isChecked = false
            }
        }
    }

    override fun initAttr(attrs: AttributeSet?) {
        super.initAttr(attrs)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollectView)
        mUncheckedColor = typedArray.getColor(R.styleable.CollectView_cv_uncheckedColor, 0)
        typedArray.recycle()
        setCheckWithExpand(true)
        setUncheckWithExpand(false)
        setAnimDuration(500)
        setAllowRevert(true)
    }

    override fun getCheckedLayoutId(): Int = R.layout.layout_collect_view_checked

    override fun getUncheckedLayoutId(): Int = R.layout.layout_collect_view_unchecked
    override fun createUncheckedView(): View {
        val view = super.createUncheckedView()
        if (view is HeartView) {
            if (mUncheckedColor != 0) {
                view.setColor(mUncheckedColor)
                view.setEdgeColor(mUncheckedColor)
            }
        }
        return view
    }
}