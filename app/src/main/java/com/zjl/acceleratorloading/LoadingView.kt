package com.zjl.acceleratorloading

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("unused")
class LoadingView2 : View {
    private var targetProgress: Int = 0
    private var pic: Drawable? = null
    private val mOCPaint: Paint = Paint()
    private val mICPaint: Paint = Paint()
    private val mTextPaint: Paint = Paint()
    private val mPicPaint: Paint = Paint()
    private val mSmallTextPaint: Paint = Paint()
    private val mArcPaint: Paint = Paint()
    private val mColorCirclePaint: Paint = Paint()
    private val mMaskPaint: Paint = Paint()
    private val mSpeedPaint: Paint = Paint()
    private val mErrorPaint: Paint = Paint()
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
    private var roteAngle = 0f
    private var outRoteAngle = 0f
    private val arcSpeed = 2.0f
    private val disappearSpeed = 15
    private val appearSpeed = 15
    private val colorCircleRotate = 2.0f
    private val colorLineRotate = 1f
    private val mMatrix = Matrix()
    private val maskMatrix = Matrix()
    private val mSpeedMatrix = Matrix()
    private val errorMatrix = Matrix()
    private lateinit var mBitmap: Bitmap
    private lateinit var mShader: BitmapShader
    private var mMaskPic: Drawable? = null
    private var mMaskBitmap: Bitmap? = null
    private var mMaskShader: BitmapShader? = null
    private var mSpeedPic: Drawable? = null
    private var mSpeedBitmap: Bitmap? = null
    private var mSpeedShader: BitmapShader? = null
    private lateinit var errorBitmap: Bitmap
    private lateinit var errorShader: BitmapShader
    private lateinit var mLinearGradient: LinearGradient
    private lateinit var mLinearGradient2: LinearGradient
    private var progressTextSize: Float = 0f
    private var errorImg: Drawable? = null
    private var isError = false
    private var isComplete = false
    private var listener: OnCompleteListener? = null
    private var onClickListener: OnClickListener? = null
    private var startTime = 0L
    private var endTime = 0L

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
        if (pic == null) {
            pic = ContextCompat.getDrawable(context, R.drawable.test)
        }
        errorImg = typeArrays.getDrawable(R.styleable.LoadingView_error_img)
        if (errorImg == null) {
            errorImg = ContextCompat.getDrawable(context, R.drawable.img_error)
        }
        mMaskPic = ContextCompat.getDrawable(context, R.drawable.img_mask)
        mMaskBitmap = mMaskPic!!.toBitmap()
        mMaskShader = BitmapShader(mMaskBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mSpeedPic = ContextCompat.getDrawable(context, R.drawable.img_speed)
        mSpeedBitmap = mSpeedPic!!.toBitmap()
        mSpeedShader = BitmapShader(mSpeedBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        progressTextSize =
            typeArrays.getDimension(
                R.styleable.LoadingView_progress_size,
                DensityUtil.sp2px(context, 64f)
            )
        typeArrays.recycle()
        mBitmap = pic!!.toBitmap()
        mShader = BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
        errorBitmap = errorImg!!.toBitmap()
        errorShader = BitmapShader(errorBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
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
        setCenterMatrix(mMatrix, mBitmap, mOuterRadius - DensityUtil.dip2px(context, 10f))
        setCenterMatrix(maskMatrix, mMaskBitmap!!, DensityUtil.dip2px(context, 153f))
        setCenterMatrix(mSpeedMatrix, mMaskBitmap!!, DensityUtil.dip2px(context, 153f))
        mMaskShader!!.setLocalMatrix(maskMatrix)
        mMaskPaint.shader = mMaskShader
        mSpeedShader!!.setLocalMatrix(mSpeedMatrix)
        mSpeedPaint.shader = mSpeedShader
        mShader.setLocalMatrix(mMatrix)

    }

    override fun onDraw(canvas: Canvas) {
        startTime = System.currentTimeMillis()
        super.onDraw(canvas)
        initPaint()
        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())
        drawCircles(canvas)
        if (!isError) {
            if (!isLoading) {
                hideAndShow(canvas)
            } else {
                drawLines(canvas)
                drawText(canvas)
                drawMovingArc(canvas)
            }
        } else {
            drawError(canvas)
        }
        updateProgress()
        endTime = System.currentTimeMillis()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (inCircle(event!!.x, event.y) && !isLoading && !isError) {
            onClickListener?.onClick()
        }
        return super.onTouchEvent(event)
    }

    private fun inCircle(x: Float, y: Float) =
        (x - width / 2).pow(2) + (y - height / 2).pow(2) < mInnerRadius.pow(2)

    private fun updateProgress() {
        if (progress == 100 && !isComplete) {
            listener?.onComplete()
            isComplete = true
        }
        if (progress < targetProgress) {
            progress++
        } else if (targetProgress == 0) {
            progress = 0
            resetAlpha()
            isComplete = false
        }
        if (progress < 100) {
            isLoading = true
        } else if (progress == 100) {
            isLoading = false
        }
    }

    private fun drawError(canvas: Canvas) {
        drawScaleLine(canvas)
        setRectCenterMatrix(
            errorMatrix,
            errorBitmap,
            mInnerRadius / sqrt(2f),
            mInnerRadius / sqrt(2f)
        )
        errorShader.setLocalMatrix(errorMatrix)
        mErrorPaint.shader = errorShader
        canvas.drawRect(
            (mCircleX - mInnerRadius) / sqrt(2f),
            (mCircleY - mInnerRadius) / sqrt(2f),
            (mCircleX + mInnerRadius) / sqrt(2f),
            (mCircleY + mInnerRadius) / sqrt(2f),
            mErrorPaint
        )
    }

    fun setProgress(progress: Int) {
        if (progress in 0..100) {
            this.targetProgress = progress
        }
    }

    fun getProgress(): Int {
        return targetProgress
    }

    fun setLogo(logo: Drawable) {
        this.pic = logo
        setLogo(pic!!.toBitmap())
    }

    fun setLogo(logo: Bitmap) {
        mBitmap = logo
        mShader = BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    fun setLogo(url: String) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    setLogo(resource!!)
                    return false
                }

            })
            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
    }

    fun setState(state: String) {
        when (state) {
            State.LOADING -> {
                targetProgress = 0
                progress = 0
                isError = false
                isLoading = true
                alpha = 255
                mMaskPaint.alpha = 255
            }
            State.ERROR -> isError = true
            State.COMPLETE -> {
                alpha = 0
                alphaStep1 = 255
                alphaStep2 = 255
                alphaStep3 = 255
                isLoading = false
                progress = 100
                targetProgress = 100
            }
        }
    }

    fun setOnCompleteListener(listener: OnCompleteListener) {
        this.listener = listener
    }

    fun setOnClickListener(listener: OnClickListener) {
        this.onClickListener = listener
    }

    private fun resetAlpha() {
        alpha = 255
        alphaStep1 = 0
        alphaStep2 = 0
        alphaStep3 = 0
        isLoading = true
    }

    private fun initParam() {
        mOuterRadius = DensityUtil.dip2px(context, 115f)
        mCircleX = 0f
        mCircleY = 0f
        mInnerRadius = DensityUtil.dip2px(context, 100f)
        mLineLength = DensityUtil.dip2px(context, 15f)
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
        roteAngle += arcSpeed
        canvas.rotate(roteAngle, mCircleX, mCircleY)
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
            drawText(canvas)
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
            mPicPaint.shader = mShader
            canvas.drawCircle(
                mCircleX,
                mCircleY,
                mOuterRadius - DensityUtil.dip2px(context, 10f),
                mPicPaint
            )
            roteAngle += colorCircleRotate
            canvas.rotate(roteAngle, mCircleX, mCircleY)
            canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mColorCirclePaint)
        }
    }

    private fun setCenterMatrix(aMatrix: Matrix, aBitmap: Bitmap, aRadius: Float) {
        val scale: Float
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

    private fun setRectCenterMatrix(
        aMatrix: Matrix,
        aBitmap: Bitmap,
        aWidth: Float,
        aHeight: Float
    ) {
        val scale: Float
        var dx = 0
        var dy = 0
        if (aBitmap.width * aWidth * 2 > aBitmap.height * aHeight * 2) {
            scale = aWidth * 2.0f / aBitmap.width
            dy = ((aHeight * 2.0f - aBitmap.height * scale) * 0.5).toInt()
        } else {
            scale = aHeight * 2.0f / aBitmap.height
            dx = ((aWidth * 2.0f - aBitmap.width * scale) * 0.5).toInt()
        }
        aMatrix.setScale(scale, scale)
        aMatrix.postTranslate(dx + mCircleX - aWidth, dy + mCircleY - aHeight)
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText("$progress", mCircleX, mCircleY + progressTextSize / 3, mTextPaint)
        canvas.drawText(
            "%",
            mCircleX + mTextPaint.measureText("$progress") / 2,
            mCircleY + progressTextSize / 3,
            mSmallTextPaint
        )
    }

    private fun drawCircles(canvas: Canvas) {
        canvas.drawCircle(mCircleX, mCircleY, mOuterRadius, mOCPaint)
        canvas.drawCircle(mCircleX, mCircleY, mInnerRadius, mICPaint)
    }

    private fun drawLines(canvas: Canvas) {
        val layer = canvas.saveLayer(
            mCircleX - width / 2,
            mCircleY - height / 2,
            mCircleX + width,
            mCircleY + height,
            null
        )
        drawScaleLine(canvas)
        outRoteAngle += colorLineRotate
        canvas.rotate(-outRoteAngle, mCircleX, mCircleY)
        canvas.drawCircle(mCircleX, mCircleY, DensityUtil.dip2px(context, 153f), mMaskPaint)
        canvas.restoreToCount(layer)
    }

    private fun drawScaleLine(canvas: Canvas) {
        canvas.drawCircle(mCircleX, mCircleY, DensityUtil.dip2px(context, 145f), mSpeedPaint)
    }

    private fun initPaint() {
        mOCPaint.apply {
            color = Color.parseColor("#282D45")
            style = Paint.Style.STROKE
            strokeWidth = DensityUtil.dip2px(context, 14f)
            isAntiAlias = true
            alpha = 255
        }
        mICPaint.apply {
            color = Color.parseColor("#282D45")
            style = Paint.Style.STROKE
            strokeWidth = DensityUtil.dip2px(context, 1f)
            isAntiAlias = true
            alpha = 255
        }
        mTextPaint.apply {
            color = Color.parseColor("#2C7FFB")
            style = Paint.Style.FILL
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = progressTextSize
            alpha = 255
        }
        mSmallTextPaint.apply {
            color = Color.parseColor("#2C7FFB")
            style = Paint.Style.FILL
            isAntiAlias = true
            textSize = (progressTextSize * 0.28).toFloat()
            alpha = 255
        }
        mArcPaint.apply {
            shader = mLinearGradient
            strokeWidth = DensityUtil.dip2px(context, 14f)
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            alpha = 255
        }
        mColorCirclePaint.apply {
            shader = mLinearGradient2
            strokeWidth = DensityUtil.dip2px(context, 14f)
            style = Paint.Style.STROKE
            isAntiAlias = true
            alpha = 255
        }
        mPicPaint.isAntiAlias = true
        mMaskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

    }

    object State {
        const val LOADING = "loading"
        const val ERROR = "error"
        const val COMPLETE = "complete"
    }

    fun interface OnCompleteListener {
        fun onComplete()
    }

    fun interface OnClickListener {
        fun onClick()
    }
}