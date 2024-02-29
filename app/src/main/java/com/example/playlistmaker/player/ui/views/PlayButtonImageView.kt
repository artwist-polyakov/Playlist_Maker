package com.example.playlistmaker.player.ui.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R
import kotlin.math.abs

class PlayButtonImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var playIconBitmap: Bitmap? = null
    private var pauseIconBitmap: Bitmap? = null
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var rect = RectF(0f, 0f, 0f, 0f)

    // состояние иконки — пауза или проигрывание
    private var currentIconState = IS_PAUSED
        set(value) {
            field = value
            invalidate()
        }

    // смена активности — инвалидирование вьюхи
    private var isActive: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlayButtonImageView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                playIconBitmap =
                    getDrawable(
                        R.styleable.PlayButtonImageView_playButtonDrawable
                    )?.toBitmap()
                pauseIconBitmap =
                    getDrawable(
                        R.styleable.PlayButtonImageView_pauseButtonDrawable
                    )?.toBitmap()
                currentIconState = getInt(
                    R.styleable.PlayButtonImageView_PlayerState,
                    0
                )

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {

        when (currentIconState) {
            IS_PLAYING -> processBitmap(playIconBitmap, canvas)
            IS_PAUSED -> processBitmap(pauseIconBitmap, canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return isActive && when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                currentIconState = (abs(currentIconState) + 1) % 2
                true
            }

            else -> {
                false
            }
        }
    }

    fun setIconState(state: Int) {
        // защищаемся от передачи невалидных значений
        currentIconState = abs(state) % 2
    }

    fun isActive(ready: Boolean) {
        when (ready) {
            true -> {
                playIconBitmap?.apply {
                    alpha = 1f
                }
                pauseIconBitmap?.apply {
                    alpha = 1f
                }
            }

            false -> {
                playIconBitmap?.apply {
                    alpha = .5f
                }
                pauseIconBitmap?.apply {
                    alpha = .5f
                }
            }
        }
        isActive = ready
    }

    private fun processBitmap(targetBitmap: Bitmap?, canvas: Canvas) {
        if (targetBitmap != null) {
            rect.right = measuredWidth.toFloat()
            rect.bottom = measuredHeight.toFloat()
            canvas.drawBitmap(targetBitmap, null, rect, null)
        }
    }

    companion object {
        const val IS_PLAYING: Int = 0
        const val IS_PAUSED: Int = 1
    }
}