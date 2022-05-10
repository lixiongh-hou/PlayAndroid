package com.viva.play.utils.bind

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.viva.play.R
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.utils.getThemeColor
import com.viva.play.views.CollectView

/**
 * @author 李雄厚
 *
 *
 */
@BindingAdapter(value = ["checked", "withAnim"])
fun bindCollectView(view: CollectView, checked: Boolean, withAnim: Boolean = true) {
    view.setChecked(checked, withAnim)
}

@BindingAdapter(value = ["tint"])
fun bindImageViewTint(view: AppCompatImageView, collected: Boolean) {
    view.setColorFilter(
        if (collected) {
            view.getThemeColor(R.attr.colorIconOnMain)
        } else {
            view.getThemeColor(R.attr.colorIconSurface)
        }
    )
}

@BindingAdapter(value = ["load"])
fun bindImageViewLoad(view: AppCompatImageView, data: String?) {
    data?.let {
        if (it.isNotEmpty() || it.startsWith("https")) {
            view.load(it)
        }
    }
}
