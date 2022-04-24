package com.viva.play.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.viva.play.R
import com.viva.play.utils.dpToPx
import com.viva.play.utils.getThemeColor

/**
 * @author 李雄厚
 *
 */
class TaoBaoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 圆环进度颜色
     */
    private var ringProgressColor = -1

    /**
     * 圆环的宽度
     */
    private var ringWidth = 0F

    /**
     * 最大值
     */
    private var ringMax = 0

    /**
     * 中间的icon
     */
    private var ringImage: Bitmap? = null

    /**
     * 中间X坐标
     */
    private var centerX = 0

    /**
     * 中间Y坐标
     */
    private var centerY = 0

    /**
     * 画笔
     */
    private var mPaint: Paint = Paint()

    /**
     * View宽度
     */
    private var mWidth = 0

    /**
     * View高度
     */
    private var mHeight = 0

    /**
     * 进度
     */
    private var progress = 0

    /**
     * 半径
     */
    private var radius = 0

    /**
     * 是否显示图标
     */
    private var isShowIcon = true

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TaoBaoView)
        ringProgressColor = typedArray.getColor(
            R.styleable.TaoBaoView_ringProgressColor,
            ringProgressColor
        )
        ringWidth = typedArray.getDimension(R.styleable.TaoBaoView_ringWidth, 5F)
        ringMax = typedArray.getInteger(R.styleable.TaoBaoView_ringMax, 50)
        ringImage = BitmapFactory.decodeResource(
            resources,
            typedArray.getResourceId(R.styleable.TaoBaoView_ringImage, R.drawable.jiantou)
        )

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mWidth = if (widthMode == MeasureSpec.AT_MOST) {
            30.dpToPx
        } else {
            widthSize
        }
        mHeight = if (heightMode == MeasureSpec.AT_MOST) {
            30.dpToPx
        } else {
            heightSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //获取中心点的位置
        centerX = width / 2
        centerY = height / 2
        radius = (centerX - ringWidth / 2).toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //确定View的宽高
        mWidth = w
        mHeight = h
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        drawProgress(canvas)
        drawImage(canvas)
    }

    /**
     * 绘制图片
     * @param canvas
     */
    private fun drawImage(canvas: Canvas?) {
        if (isShowIcon) {
            canvas?.drawBitmap(
                ringImage!!,
                centerX - ringImage!!.width / 2.toFloat(),
                centerY - ringImage!!.height / 2.toFloat(),
                mPaint
            )
        }
    }

    /**
     * 绘制进度条
     * @param canvas
     */
    private fun drawProgress(canvas: Canvas?) {
        mPaint.isAntiAlias = true
        mPaint.color = ringProgressColor
        mPaint.strokeWidth = ringWidth
        //设置画笔样式
        mPaint.style = Paint.Style.STROKE
        val rectF =
            RectF(
                (centerX - radius).toFloat(),
                (centerY - radius).toFloat(),
                (centerX + radius).toFloat(),
                (centerY + radius).toFloat()
            )
        //绘制圆弧
        canvas?.drawArc(rectF, -110f, -360 * progress / ringMax.toFloat(), false, mPaint)
    }

    /**
     * 设置进度
     * @param progress
     */
    @Synchronized
    fun setProgress(progress: Int) {
        var pos = progress
        if (pos < 0) {
            pos = 0
        }
        if (pos >= ringMax) {
            pos = ringMax
        }
        this.progress = pos
        postInvalidate()
    }

    /**
     * 设置是否显示图标
     * @param isShow
     */
    @Synchronized
    fun setIsShowIcon(isShow: Boolean) {
        isShowIcon = isShow
    }

}