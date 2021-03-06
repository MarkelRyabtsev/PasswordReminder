package com.markel.passwordreminder.ext

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import androidx.core.animation.doOnEnd

inline fun getValueAnimator(
    forward: Boolean = true,
    newDuration: Long,
    newInterpolator: TimeInterpolator,
    crossinline updateListener: (progress: Float) -> Unit
) = (
        if (forward) ValueAnimator.ofFloat(0f, 1f)
        else ValueAnimator.ofFloat(1f, 0f)
    ).apply {
        addUpdateListener { updateListener(it.animatedValue as Float) }
        duration = newDuration
        interpolator = newInterpolator
    }

fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio

    val a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio)
    val r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio)
    val g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio)
    val b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio)
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

fun ProgressBar.setProgressAnimation(animDuration: Long, finished: () -> Unit) {
    ObjectAnimator.ofInt(
        this,
        "progress",
        100,
        0
    ).apply {
        duration = animDuration
        interpolator = LinearInterpolator()
        doOnEnd {
            finished.invoke()
        }
    }.start()
}

inline val Context.screenWidth: Int
    get() = Point().also { (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(it) }.x
inline val View.screenWidth: Int
    get() = context!!.screenWidth

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)