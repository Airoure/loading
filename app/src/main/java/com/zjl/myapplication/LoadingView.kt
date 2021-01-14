package com.zjl.myapplication

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class LoadingView : View {
    private var pic: Drawable? = null
    private val mOCPaint: Paint = Paint()
    private val mICPaint: Paint = Paint()
    private val mTextPaint: Paint = Paint()
    private val mPicPaint: Paint = Paint()
    private val mSmallTextPaint: Paint = Paint()
    private val mArcPaint: Paint = Paint()
    private val mColorCirclePaint: Paint = Paint()
    private val mColorLinePaint: Paint = Paint()
    private var mOuterRadius = 0f
    private var mCircleX = 0f
    private var mCircleY = 0f
    private var mInnerRadius = 0f
    private var mLineLength = 0f
    private var progress = 0
    private var isLoading = true
    private var alpha = 255
    private var alphaStep1 = 0
    private var alphaStep2 = 0
    private var alphaStep3 = 0
    private var rotateAngle = 0f
    private var outRoateAngle = 0f
    private val arcSpeed = 1.2f
    private val disappearSpeed = 5
    private val appearSpeed = 5
    private val colorCircleRotate = 0.6f
    private val colorLineRotate = 0.6f
    private val lengthArray = FloatArray(360)
    private val mMatrix = Matrix()
    private var scale: Float = 0f
    private var dx = 0
    private var dy = 0
    private lateinit var mBitmap: Bitmap
    private lateinit var mShader: BitmapShader
    private lateinit var mLinearGradient: LinearGradient
    private lateinit var mLinearGradient2: LinearGradient

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attributeSet,
            defStyleAttr
    ) {
        if (attributeSet == null) return
        val typeArrays = context.obtainStyledAttributes(attributeSet, R.styleable.LoadingView)
        pic = typeArrays.getDrawable(R.styleable.LoadingView_img)
        typeArrays.recycle()
        mBitmap = pic!!.toBitmap()
        mShader = BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Thread {
            while (true) {
                if (progress < 100) {

                    isLoading = true
                    mHandler.sendEmptyMessage(0)
                    Thread.sleep(10)
                }
                if (progress == 100) {
                    isLoading = false
                    mHandler.sendEmptyMessage(0)
                    Thread.sleep(10)
                }
            }
        }.start()
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(widthMeasureSpec)
        val mWidth = 230
        val mHeight = 230
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight)
        }
        if (widthMode == MeasureSpec.AT_MOST && heightMode != MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSize)
        }
        if (widthMode != MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mHeight)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initParam()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initPaint()
        canvas!!.translate((width / 2).toFloat(), (height / 2).toFloat())
        drawCircles(canvas)
        drawLines(canvas)
        if (isLoading == false) {
            hideAndShow(canvas)
        } else {
            drawColorLines(canvas)
            drawText(canvas)
            drawMovingArc(canvas)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mBitmap.recycle()
    }

    fun setProgress(progress: Int) {
        if (progress in 0..100) {
            this.progress = progress
        }
        if (progress == 0) {
            resetAlpha()
        }
    }

    fun setLogo(logo: Drawable) {
        this.pic = logo
        mBitmap = pic!!.toBitmap()
        mShader = BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
    }

    private fun resetAlpha() {
        alphaStep1 = 0
        alphaStep2 = 0
        alphaStep3 = 0
    }

    private fun initParam() {
        mOuterRadius = Math.min(width, height) * 0.7.toFloat() / 2
        mCircleX = 0f
        mCircleY = 0f
        mInnerRadius = (mOuterRadius * 0.9).toFloat()
        mLineLength = mOuterRadius / 9
        for (i in 0..359) {
            lengthArray[i] = Random().nextFloat() * mLineLength * 2
        }
        mLinearGradient = LinearGradient(
                mCircleX, mCircleY - mOuterRadius, mCircleX + mOuterRadius, mCircleY,
                intArrayOf(
                        Color.parseColor("#001DA0FF"),
                        Color.parseColor("#2C41FB"),
                        Color.parseColor("#FF26CF")
                ),
                floatArrayOf(0.09f, 0.62f, 0.99f),
                Shader.TileMode.CLAMP
        )
        mLinearGradient2 = LinearGradient(
                mCircleX, mCircleY - mOuterRadius, mCircleX + mOuterRadius, mCircleY,
                intArrayOf(
                        Color.parseColor("#1DA0FF"),
                        Color.parseColor("#2C41FB"),
                        Color.parseColor("#E547FF"),
                        Color.parseColor("#FF26CF")
                ),
                floatArrayOf(0.0f, 0.23f, 0.89f, 0.99f),
                Shader.TileMode.CLAMP
        )
    }

    private fun drawMovingArc(canvas: Canvas) {
        rotateAngle += arcSpeed
        canvas.rotate(rotateAngle, mCircleX, mCircleY)
        canvas.drawArc(
                mCircleX - mOuterRadius,
                mCircleY - mOuterRadius,
                mCircleX + mOuterRadius,
                mCircleY + mOuterRadius,
                180f,
                270f,
                false,
                mArcPaint
        )
    }

    private fun hideAndShow(canvas: Canvas) {
        if (alpha > 0) {
            alpha -= disappearSpeed
            mTextPaint.alpha = alpha
            mSmallTextPaint.alpha = alpha
            mArcPaint.alpha = alpha
            mColorLinePaint.alpha = alpha
            drawColorLines(canvas)
            canvas.drawText("${progress}", mCircleX, mCircleY + 50, mTextPaint)
            canvas.drawText(
                    "%",
                    mCircleX + mTextPaint.measureText("${progress}") / 2,
                    mCircleY + 50,
                    mSmallTextPaint
            )
            drawMovingArc(canvas)
        } else {
            if (mBitmap.width * mOuterRadius * 2 > mBitmap.height * mOuterRadius * 2) {
                scale = mOuterRadius * 2.0f / mBitmap.width
                dy = ((mOuterRadius * 2.0f - mBitmap.height * scale) * 0.5).toInt()
            } else {
                scale = mOuterRadius * 2.0f / mBitmap.height
                dx = ((mOuterRadius * 2.0f - mBitmap.width * scale) * 0.5).toInt()
            }
            mMatrix.setScale(scale, scale)
            mMatrix.postTranslate(dx + mCircleX - mOuterRadius, dy + mCircleY - mOuterRadius)
            mShader.setLocalMatrix(mMatrix)
            mPicPaint.setShader(mShader)
            mColorLinePaint.alpha = 0
            mColorCirclePaint.alpha = 0
            mPicPaint.alpha = 0
            if (alphaStep1 < 255) {
                alphaStep1 += appearSpeed
                alphaStep3 += appearSpeed / 2
                mColorLinePaint.alpha = alphaStep1
                mPicPaint.alpha = alphaStep3
            } else if (alphaStep2 < 255) {
                alphaStep2 += appearSpeed
                alphaStep3 += appearSpeed / 2
                mColorLinePaint.alpha = 255
                mColorCirclePaint.alpha = alphaStep2
                mPicPaint.alpha = alphaStep3
            } else {
                mColorLinePaint.alpha = 255
                mColorCirclePaint.alpha = 255
                mPicPaint.alpha = 255
            }
            drawColorLines(canvas)
            canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mPicPaint)
            rotateAngle += colorCircleRotate
            canvas.rotate(rotateAngle, mCircleX, mCircleY)
            canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mColorCirclePaint)
        }
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText("${progress}", mCircleX, mCircleY + 50, mTextPaint)
        canvas.drawText(
                "%",
                mCircleX + mTextPaint.measureText("${progress}") / 2,
                mCircleY + 50,
                mSmallTextPaint
        )
    }

    private fun drawColorLines(canvas: Canvas) {
        canvas.save()
        outRoateAngle += colorLineRotate
        canvas.rotate(-outRoateAngle, mCircleX, mCircleY)
        for (i in 0..359 step 2) {
            canvas.drawLine(
                    ((mOuterRadius + dip2px(7)) * sin(2 * PI / 360 * i)).toFloat(),
                    ((mOuterRadius + dip2px(7)) * cos(2 * PI / 360 * i)).toFloat(),
                    ((lengthArray[i] + mOuterRadius + dip2px(7)) * sin(2 * PI / 360 * i)).toFloat(),
                    ((lengthArray[i] + mOuterRadius + dip2px(7)) * cos(2 * PI / 360 * i)).toFloat(),
                    mColorLinePaint
            )
        }
        canvas.restore()
    }

    private fun drawCircles(canvas: Canvas) {
        canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mOCPaint)
        canvas.drawCircle(mCircleX, mCircleY, mInnerRadius, mICPaint)
    }

    private fun drawLines(canvas: Canvas) {
        for (i in 0..359 step 2) {
            canvas.drawLine(
                    mCircleX,
                    mCircleY - mOuterRadius - dip2px(7),
                    mCircleX,
                    mCircleY - mOuterRadius - dip2px(14) - mLineLength,
                    mICPaint
            )
            canvas.rotate(2f, mCircleX, mCircleY)
        }
    }

    private fun initPaint() {
        mOCPaint.apply {
            color = Color.parseColor("#282D45")
            style = Paint.Style.STROKE
            strokeWidth = dip2px(7)
            isAntiAlias = true
            alpha = 255
        }
        mICPaint.apply {
            color = Color.parseColor("#282D45")
            style = Paint.Style.STROKE
            strokeWidth = dip2px(1)
            isAntiAlias = true
            alpha = 255
        }
        mTextPaint.apply {
            color = Color.parseColor("#2C7FFB")
            style = Paint.Style.FILL
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = 150f
            alpha = 255
        }
        mSmallTextPaint.apply {
            color = Color.parseColor("#2C7FFB")
            style = Paint.Style.FILL
            isAntiAlias = true
            textSize = 50f
            alpha = 255
        }
        mArcPaint.apply {
            shader = mLinearGradient
            strokeWidth = dip2px(7)
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            alpha = 255
        }
        mColorCirclePaint.apply {
            shader = mLinearGradient2
            strokeWidth = dip2px(9)
            style = Paint.Style.STROKE
            isAntiAlias = true
            alpha = 255
        }
        mColorLinePaint.apply {
            shader = mLinearGradient2
            strokeWidth = dip2px(1)
            style = Paint.Style.FILL
            isAntiAlias = true
            alpha = 255
        }
    }

    private fun dip2px(dipValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }
}