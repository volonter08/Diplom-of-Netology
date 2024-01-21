package ru.netology.nmedia.entity.utills

import android.animation.Animator
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.io.Serializable
import kotlin.math.sqrt


class AndroidUtils {
    companion object {
        fun hideKeyBoard(view: View) {
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyBoard(view: View) {
            val imm =  (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            if (view.requestFocus())
                view.postDelayed( {
                    view.requestFocus()
                    imm.showSoftInput(view,0)
                },100
                )
        }
        fun registerCircularRevealAnimation(
            view: View,
            revealSettings: RevealAnimationSetting,
        ) {
            view.addOnLayoutChangeListener(object : OnLayoutChangeListener {
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
                        sqrt((width * width + height * height).toDouble()).toFloat()
                    val anim: Animator =
                        ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius)
                            .setDuration(duration.toLong())
                    anim.interpolator = FastOutSlowInInterpolator()
                    anim.start()
                }
            })
        }

    }
}

data class RevealAnimationSetting(
    val centerX:Int,
    val centerY:Int,
    val width:Int,
    val height:Int
):Serializable{
    companion object{
        fun create(point: Point,width: Int,height: Int): RevealAnimationSetting {
            return RevealAnimationSetting(
                point.x,
                point.y,
                width,
                height
            )
        }
    }
}