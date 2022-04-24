package com.viva.play.utils

import com.kennyc.view.MultiStateView
import com.viva.play.R

/**
 * @author 李雄厚
 *
 *
 */

fun MultiStateView.isContent(): Boolean {
    return viewState == MultiStateView.ViewState.CONTENT
}

fun MultiStateView.toLoading() {
    viewState = MultiStateView.ViewState.LOADING
}

fun MultiStateView.toEmpty(
    force: Boolean = false,
    icon: Int? = R.drawable.ic_empty,
    text: String? = "这什么都没有啊~~",
    listener: (() -> Unit)? = null
) {
    viewState = MultiStateView.ViewState.EMPTY
    getView(MultiStateView.ViewState.EMPTY)?.setOnClickListener {
        listener?.invoke()
    }
}

fun MultiStateView.toError(
    force: Boolean = false,
    icon: Int? = R.drawable.ic_error,
    text: String? = "加载出现了问题！",
    listener: (() -> Unit)? = null
) {
    viewState = MultiStateView.ViewState.ERROR
    getView(MultiStateView.ViewState.ERROR)?.setOnClickListener {
        listener?.invoke()
    }
}

fun MultiStateView.toContent() {
    viewState = MultiStateView.ViewState.CONTENT
}