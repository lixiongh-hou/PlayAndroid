package com.viva.play.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.annotation.FloatRange
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.viva.play.R
import com.viva.play.utils.dpToPx
import com.viva.play.utils.screenWidth
import java.lang.reflect.ParameterizedType


/**
 * @author 李雄厚
 *
 * @features DialogFragment基类
 */
abstract class BaseFragmentDialog<Binding : ViewDataBinding> : AppCompatDialogFragment() {
    lateinit var binding: Binding

    /**背景昏暗度 */
    private var mDimAmount = 0.5F

    /**左右边距 */
    private var mMargin = 0

    /**进入退出动画 */
    private var mAnimStyle = 0

    private var mGravity = Gravity.CENTER

    /**点击外部取消 */
    private var mOutCancel = true
    private var params: WindowManager.LayoutParams? = null
    var mContext: Context? = null
    private var mWidth = 0
    private var mHeight = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.style_dialog_fuzzy)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBindingWithModel(inflater)
        return binding.root
    }

    private fun initBindingWithModel(inflater: LayoutInflater) {
        val type = javaClass.genericSuperclass as ParameterizedType
        val actualTypeArguments = type.actualTypeArguments
        for (argument in actualTypeArguments) {
            val clazz = argument as Class<*>
            if (clazz.superclass == ViewDataBinding::class.java) {
                val asSubclass = clazz.asSubclass(ViewDataBinding::class.java)
                val declaredMethod =
                    asSubclass.getDeclaredMethod("inflate", LayoutInflater::class.java)
                @Suppress("UNCHECKED_CAST")
                binding = declaredMethod.invoke(this, inflater) as Binding
                convertView(binding)
            }
        }
        if (!::binding.isInitialized) {
            throw IllegalStateException("Binding 必须是ViewDataBinding的子类")
        }
    }

    override fun onStart() {
        super.onStart()
        initParams()
    }

    private fun initParams() {
        val window = dialog?.window
        if (window != null) {
            params = window.attributes
            params?.dimAmount = mDimAmount
            params?.gravity = mGravity
            //设置dialog宽度
            if (mWidth == 0) {
                params?.width = screenWidth - 2 * mMargin.toFloat().dpToPx
            } else {
                params?.width = mWidth.toFloat().dpToPx
            }

            //设置dialog高度
            if (mHeight == 0) {
                params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                params?.height = mHeight.toFloat().dpToPx
            }
            //设置dialog动画
            if (mAnimStyle != 0) {
                window.setWindowAnimations(mAnimStyle)
            }
            window.attributes = params
        }
        //是否点击空白处隐藏
        dialog?.setCanceledOnTouchOutside(mOutCancel)
        dialog?.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                //是否拦截返回按钮关闭对话框
                return@setOnKeyListener false
            }
            false
        }
    }

    /**
     * 设置背景昏暗度
     *
     * @param dimAmount
     * @return
     */
    fun setDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float) {
        mDimAmount = dimAmount
    }

    /**
     * 弹出框位置
     */
    fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    /**
     * 设置宽高
     *
     * @param width 固定宽度，默认为0，自适应宽度 当设置宽度 >0 {setMargin()} 无效
     * @param height 固定高度，默认为0,自适应高度
     * @return
     */
    fun setSize(width: Int = 0, height: Int = 0) {
        mWidth = width
        mHeight = height
    }

    /**
     * 设置左右margin
     */
    open fun setMargin(margin: Int) {
        mMargin = margin
    }

    /**
     * 设置进入退出动画
     */
    fun setAnimStyle(@StyleRes animStyle: Int) {
        mAnimStyle = animStyle
    }

    /**
     * 设置是否点击外部取消
     * @return
     */
    fun setOutCancel(outCancel: Boolean) {
        mOutCancel = outCancel
    }

    fun show(manager: FragmentManager?) {
        super.show(manager!!, System.currentTimeMillis().toString())
    }


    /**
     * 操作dialog布局
     */
    abstract fun convertView(binding: Binding)

    override fun show(manager: FragmentManager, tag: String?) {
        if (isAdded || isResumed || isVisible) {
            return
        }
        super.show(manager, tag)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        if (isAdded || isResumed || isVisible) {
            return -1
        }
        return super.show(transaction, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (isAdded || isResumed || isVisible) {
            return
        }
        super.showNow(manager, tag)
    }
}