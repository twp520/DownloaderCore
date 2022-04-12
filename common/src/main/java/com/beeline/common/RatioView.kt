package com.beeline.common

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout


class RatioView : FrameLayout {

    private var baseWidth = true
    private var ratio: Float = 1f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        if (null != attrs) {
            val typeArray =
                context.obtainStyledAttributes(attrs, R.styleable.RatioView)
            baseWidth = typeArray.getBoolean(R.styleable.RatioView_baseWidth, true)
            ratio = typeArray.getFloat(R.styleable.RatioView_ratio, 1f)
            typeArray.recycle()
        }
    }

    fun setRatio(ratio: Float) {
        if (this.ratio != ratio && ratio > 0) {
            this.ratio = ratio
            requestLayout()
        }
    }

    fun setBaseWidth(baseWidth: Boolean) {
        this.baseWidth = baseWidth
        requestLayout()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (baseWidth) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width / ratio).toInt()
            setMeasuredDimension(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            val width = (height * ratio).toInt()
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), height)
        }
    }
}