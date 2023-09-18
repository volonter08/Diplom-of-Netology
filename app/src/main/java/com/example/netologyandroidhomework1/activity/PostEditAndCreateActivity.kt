package com.example.netologyandroidhomework1.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.netologyandroidhomework1.R
import com.example.netologyandroidhomework1.databinding.ActivityPostEditAndCreateBinding
import com.example.netologyandroidhomework1.dto.Post
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostEditAndCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostEditAndCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val editText = binding.editText.apply {
            text.clear()
        }
        val button = binding.save
        val cancelButton = binding.cancelButton
        val typeOfOperation = intent.getSerializableExtra("typeOfOperation") as TypeOfOperationForStartNewActivity
        when( typeOfOperation){
            TypeOfOperationForStartNewActivity.CREATING -> {
                button.setIconResource(R.drawable.baseline_create_24)
                button.setText("CREATE")
                binding.linearLayoutUpdate.visibility = GONE
                val newIntent= Intent()
                button.setOnClickListener{
                    if (editText.text.isBlank())
                        setResult(Activity.RESULT_CANCELED,newIntent)
                    else {
                        newIntent.putExtra(Intent.EXTRA_TEXT,editText.text.toString())
                        setResult(Activity.RESULT_OK,newIntent)
                    }
                    finish()
                }
            }
            else -> {
                button.setIconResource(R.drawable.baseline_save_as_24)
                button.setText("EDIT")
                val post = intent.getSerializableExtra("post") as Post
                binding.updateContentText.setText(post.content)
                editText.setText(post.content)
                binding.linearLayoutUpdate.visibility = VISIBLE
                val newIntent= Intent()
                button.setOnClickListener{
                    if (editText.text.isBlank()) {
                        newIntent.putExtra("isCancelButtonClicked", false)
                        setResult(Activity.RESULT_CANCELED, newIntent)
                    }
                    else {
                        newIntent.putExtra("post",post.copy(content = editText.text.toString()) )
                        setResult(Activity.RESULT_OK,newIntent)
                    }
                    finish()
                }
                cancelButton.setOnClickListener {
                    newIntent.putExtra("post",post)
                    setResult(Activity.RESULT_CANCELED,newIntent)
                    finish()
                }
            }
        }
    }
}