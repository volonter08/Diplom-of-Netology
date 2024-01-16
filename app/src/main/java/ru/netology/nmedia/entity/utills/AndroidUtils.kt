package com.example.netologyandroidhomework1

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.io.Serializable


class AndroidUtils {
    companion object {
        fun hideKeyBoard(view: View) {
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyBoard(view: View) {
            val imm =  (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            if (view.requestFocus())
                view.postDelayed(Runnable {
                    view.requestFocus()
                    imm.showSoftInput(view,0)
                },100
                )
        }
        fun registerCircularRevealAnimation(
            context: Context,
            view: View,
            revealSettings: RevealAnimationSetting,
            startColor: Int,
            endColor: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.addOnLayoutChangeListener(object : OnLayoutChangeListener {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onLayoutChange(
                        v: View,
                        left: Int,
                        top: Int,
                        right: Int,
                        bottom: Int,
                        oldLeft: Int,
                        oldTop: Int,
                        oldRight: Int,
                        oldBottom: Int
                    ) {
                        v.removeOnLayoutChangeListener(this)
                        val cx: Int = revealSettings.centerX
                        val cy: Int = revealSettings.centerY
                        val width: Int = revealSettings.width
                        val height: Int = revealSettings.height
                        val duration = 2000

                        //Simply use the diagonal of the view
                        val finalRadius =
                            Math.sqrt((width * width + height * height).toDouble()).toFloat()
                        val anim: Animator =
                            ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                                .setDuration(duration.toLong())
                        anim.setInterpolator(FastOutSlowInInterpolator())
                        anim.start()
                    }
                })
            }
        }
        fun startCircularExitAnimation(
            context: Context,
            view: View?,
            revealSettings: RevealAnimationSetting,
            startColor: Int,
            endColor: Int,
            listener: Dismissible.OnDismissedListener
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = revealSettings.centerX
                val cy = revealSettings.centerY
                val width = revealSettings.width
                val height = revealSettings.height
                val duration = context.resources.getInteger(R.integer.config_mediumAnimTime)
                val initRadius = Math.sqrt((width * width + height * height).toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f)
                anim.duration = duration.toLong()
                anim.interpolator = FastOutSlowInInterpolator()
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        listener.onDismissed()
                    }
                })
                anim.start()
                startColorAnimation(view!!, startColor, endColor, duration)
            } else {
                listener.onDismissed()
            }
        }
        fun startColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
            val anim = ValueAnimator()
            anim.setIntValues(startColor, endColor)
            anim.setEvaluator(ArgbEvaluator())
            anim.addUpdateListener { valueAnimator -> view.setBackgroundColor((valueAnimator.animatedValue as Int)) }
            anim.duration = duration.toLong()
            anim.start()
        }
    }
}

data class RevealAnimationSetting(
    val centerX:Int,
    val centerY:Int,
    val width:Int,
    val height:Int
):Serializable
interface Dismissible {
    interface OnDismissedListener {
        fun onDismissed()
    }

    fun dismiss(listener: OnDismissedListener)
}