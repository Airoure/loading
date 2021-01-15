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

    private val mMaskPaint: Paint = Paint()
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
    private val maskMatrix = Matrix()
    private lateinit var mBitmap: Bitmap
    private lateinit var mShader: BitmapShader
    private val mMaskBitmap = context.getDrawable(R.drawable.img_mask)!!.toBitmap()
    private val mMaskShader = BitmapShader(mMaskBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
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
        if (isLoading == false) {
            hideAndShow(canvas)
        } else {
            drawLines(canvas)
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
        isLoading = true
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
            mMaskPaint.alpha = alpha
            drawLines(canvas)
            canvas.drawText("${progress}", mCircleX, mCircleY + 50, mTextPaint)
            canvas.drawText(
                    "%",
                    mCircleX + mTextPaint.measureText("${progress}") / 2,
                    mCircleY + 50,
                    mSmallTextPaint
            )
            drawMovingArc(canvas)
        } else {
            mColorCirclePaint.alpha = 0
            mPicPaint.alpha = 0
            mMaskPaint.alpha = 0
            if (alphaStep1 < 255) {
                alphaStep1 += appearSpeed
                alphaStep3 += appearSpeed / 2
                mMaskPaint.alpha = alphaStep1
                mPicPaint.alpha = alphaStep3
            } else if (alphaStep2 < 255) {
                alphaStep2 += appearSpeed
                alphaStep3 += appearSpeed / 2
                mColorCirclePaint.alpha = alphaStep2
                mPicPaint.alpha = alphaStep3
                mMaskPaint.alpha = 255
            } else {
                mMaskPaint.alpha = 255
                mColorCirclePaint.alpha = 255
                mPicPaint.alpha = 255
            }
            drawLines(canvas)
            setCenterMatrix(mMatrix, mBitmap, mOuterRadius)
            mShader.setLocalMatrix(mMatrix)
            mPicPaint.setShader(mShader)
            canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mPicPaint)
            rotateAngle += colorCircleRotate
            canvas.rotate(rotateAngle, mCircleX, mCircleY)
            canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mColorCirclePaint)
        }
    }

    private fun setCenterMatrix(aMatrix: Matrix, aBitmap: Bitmap, aRadius: Float) {
        var scale = 0f
        var dx = 0
        var dy = 0
        if (aBitmap.width * aRadius * 2 > aBitmap.height * aRadius * 2) {
            scale = aRadius * 2.0f / aBitmap.width
            dy = ((aRadius * 2.0f - aBitmap.height * scale) * 0.5).toInt()
        } else {
            scale = aRadius * 2.0f / aBitmap.height
            dx = ((aRadius * 2.0f - aBitmap.width * scale) * 0.5).toInt()
        }
        aMatrix.setScale(scale, scale)
        aMatrix.postTranslate(dx + mCircleX - aRadius, dy + mCircleY - aRadius)
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

    private fun drawCircles(canvas: Canvas) {
        canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mOCPaint)
        canvas.drawCircle(mCircleX, mCircleY, mInnerRadius, mICPaint)
    }

    private fun drawLines(canvas: Canvas) {
        val layer = canvas.saveLayer(mCircleX - width / 2, mCircleY - height / 2, mCircleX + width, mCircleY + height, null)
        outRoateAngle += colorLineRotate
        canvas.rotate(-outRoateAngle, mCircleX, mCircleY)
        for (i in 0..359 step 2) {
            canvas.drawLine(
                    mCircleX,
                    mCircleY - mOuterRadius - dip2px(7),
                    mCircleX,
                    mCircleY - mOuterRadius - dip2px(7) - mLineLength,
                    mICPaint
            )
            canvas.rotate(2f, mCircleX, mCircleY)
        }
        setCenterMatrix(maskMatrix, mMaskBitmap, mOuterRadius + dip2px(14) + mLineLength)
        mMaskShader.setLocalMatrix(maskMatrix)
        mMaskPaint.setShader(mMaskShader)
        mMaskPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP))
        canvas.drawCircle(mCircleX, mCircleY, mOuterRadius + dip2px(7) + mLineLength, mMaskPaint)
        canvas.restoreToCount(layer)
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
    }

    private fun dip2px(dipValue: Int): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }
}