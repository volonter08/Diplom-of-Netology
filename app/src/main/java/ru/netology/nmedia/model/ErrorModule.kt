package ru.netology.nmedia.model

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.R

@Module
@InstallIn(FragmentComponent::class)
object ErrorModule {
    @Provides
    @FragmentScoped
    fun provideErrorCallback(@ActivityContext context: Context):ErrorCallback{
        return object:ErrorCallback{
            override fun onError(reason:String?,onRetryListener: OnRetryListener) {
                MaterialAlertDialogBuilder(context).setTitle(R.string.request_is_not_successful).setMessage(reason).setPositiveButton("RETRY"){ _, _ ->
                    onRetryListener.onRetry()
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
}