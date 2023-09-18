package com.example.netologyandroidhomework1.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import com.example.netologyandroidhomework1.dto.Post

class EditPostActivityContract() : ActivityResultContract<Post, Post?>() {
    override fun createIntent(context: Context, input: Post): Intent {
        return Intent(context, PostEditAndCreateActivity::class.java).apply {
            putExtra("typeOfOperation", TypeOfOperationForStartNewActivity.EDITING)
            putExtra("post",input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Post? {
        return if (resultCode== Activity.RESULT_OK)
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) intent?.getSerializableExtra("post",
                Post::class.java)
            else intent?.getSerializableExtra("post") as Post
        else
            null
    }
}