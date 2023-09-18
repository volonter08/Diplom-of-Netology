package com.example.netologyandroidhomework1

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.netologyandroidhomework1.activity.PostEditAndCreateActivity
import com.example.netologyandroidhomework1.activity.TypeOfOperationForStartNewActivity

class NewPostActivityContract() : ActivityResultContract<Unit, String?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, PostEditAndCreateActivity::class.java).apply {
            putExtra("typeOfOperation", TypeOfOperationForStartNewActivity.CREATING)
        }
    }
    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return if (resultCode== Activity.RESULT_OK)
            intent?.let{
                it.getStringExtra(Intent.EXTRA_TEXT)
            }
        else
            null
    }
}