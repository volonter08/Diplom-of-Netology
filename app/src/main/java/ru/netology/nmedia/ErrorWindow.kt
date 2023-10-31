package ru.netology.nmedia

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ErrorWindow {
    companion object{
        fun show(context: Context,reason:String?, onRetry:()->Unit){
            MaterialAlertDialogBuilder(context).setTitle(R.string.request_is_not_successful).setMessage(reason).setPositiveButton("RETRY"){ dialog, which->
                onRetry()
            }.create().apply {
                window?.setGravity(Gravity.TOP)
                ObjectAnimator.ofObject(this.window,"attributes",object :
                    TypeEvaluator<WindowManager.LayoutParams> {
                    override fun evaluate(
                        fraction: Float,
                        startValue: WindowManager.LayoutParams,
                        endValue: WindowManager.LayoutParams
                    ): WindowManager.LayoutParams {
                        val attr=  WindowManager.LayoutParams()
                        attr.copyFrom(window?.attributes)
                        return attr.apply {
                            y = (startValue.y + (endValue.y - startValue.y)*fraction).toInt()
                        }
                    }
                }, WindowManager.LayoutParams().apply { y = 2000}).apply {
                    duration = 40000
                    start()
                }
            }.show()
        }
    }
}