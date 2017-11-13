package xyz.mayahiro.fishbowl.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Created by mayahiro on 2017/11/07.
 */
class FishbowlView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    // for draw
    private val paint = Paint()
    private val wavePaint = Paint()

    // for shader
    private var waveShader: BitmapShader? = null
    private var bitmapBuffer: Bitmap? = null
    private var shaderMatrix: Matrix = Matrix()

    // for animation
    private var waveShiftRatio: Float = 0f

    fun setWaveShiftRatio(value: Float) {
        waveShiftRatio = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        waveShader?.let {
            val centerX = canvas.width / 2f
            val centerY = canvas.height / 2f

            // wave
            shaderMatrix.reset()
            shaderMatrix.postTranslate(waveShiftRatio * width, 0f)
            it.setLocalMatrix(shaderMatrix)
            canvas.drawCircle(centerX, centerY, centerX, wavePaint)

            // border
            paint.color = Color.BLACK
            paint.strokeWidth = 8f
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(centerX, centerY, centerX - (paint.strokeWidth / 2), paint)
        }

        super.onDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updateWaveSharder()
    }

    private fun updateWaveSharder() {
        if (bitmapBuffer == null) {
            val width = measuredWidth
            val height = measuredHeight

            if (width > 0 && height > 0) {
                val angularFrequency = Math.PI / width
                val amplitude = height * 0.03f
                val waterLevel = height * 0.8f

                bitmapBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmapBuffer)

                val paint = Paint()
                paint.strokeWidth = 2f
                paint.isAntiAlias = true

                val endX = width + 1
                val endY = height + 1

                paint.color = Color.BLUE
                for (x in 0..endX) {
                    canvas.drawLine(x.toFloat(), (waterLevel + amplitude * Math.sin(x * angularFrequency)).toFloat(), x.toFloat(), endY.toFloat(), paint)
                }

                waveShader = BitmapShader(bitmapBuffer, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP)
                wavePaint.shader = waveShader
            }
        }
    }
}
