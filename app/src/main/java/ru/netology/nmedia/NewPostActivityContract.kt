package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.activity.PostEditAndCreateActivity
import ru.netology.nmedia.activity.TypeOfOperationForStartNewActivity

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