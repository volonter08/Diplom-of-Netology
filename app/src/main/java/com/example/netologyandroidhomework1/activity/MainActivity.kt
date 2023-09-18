package com.example.netologyandroidhomework1.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.*
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.netologyandroidhomework1.NewPostActivityContract
import com.example.netologyandroidhomework1.OnButtonTouchListener
import com.example.netologyandroidhomework1.R
import com.example.netologyandroidhomework1.adapter.PostAdapter
import com.example.netologyandroidhomework1.databinding.ActivityMainBinding
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.viewModel.PostViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    val viewModel: PostViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        val newPostLauncher = registerForActivityResult(NewPostActivityContract()){
            if(it==null){
                Snackbar.make(viewBinding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){

                }.show()
            }
            else {
                viewModel.createPost(it)
            }
        }
        val editPostLauncher = registerForActivityResult(EditPostActivityContract()){ post->
            if(post==null){
                Snackbar.make(viewBinding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){

                }.show()
            }
            else viewModel.update(post)
        }
        val postOnButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(id:Long) {
                viewModel.like(id)
            }
            override fun onDislikeCLick(id: Long) {
                viewModel.dislike(id)
            }
            override fun onShareCLick(post: Post){
                val intentSend = Intent().apply {
                    action= Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,post.content)
                    type = "text/plain"
                }
                val chooserIntentSend = Intent.createChooser(intentSend, "getString(R.string.)")
                startActivity(chooserIntentSend)
            }
            override fun onRemoveClick(id: Long) {
                viewModel.remove(id)
            }
            override fun onUpdateCLick(post: Post) {
                editPostLauncher.launch(post)
            }
            override fun onCreateClick() {
                newPostLauncher.launch()
            }
        }
        val postAdapter = PostAdapter(context = applicationContext,postOnButtonTouchListener)
        viewBinding.recycleView.adapter = postAdapter
        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
        }
        viewModel.dataState.observe(this) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                viewBinding.swipeRefreshLayout.isRefreshing = isRefreshed
                when{
                    error -> {
                        MaterialAlertDialogBuilder(this@MainActivity).setTitle(R.string.request_is_not_successful).setPositiveButton("OK"){ dialog, which->
                            feedModel.errorRetryListener?.onRetry()
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
                            },WindowManager.LayoutParams().apply { y = 2000}).apply {
                                duration = 40000
                                start()
                            }
                        }.show()
                    }

                }
            }
        }
        viewModel.data.observe(this){
            viewBinding.progressBar.isVisible= false
            viewBinding.emtyOrErrorMessage.isVisible = it.posts.isEmpty()
            postAdapter.submitList(it.posts)
        }
        viewBinding.createButton.setOnClickListener {
            postOnButtonTouchListener.onCreateClick()
        }
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }
            val token = task.result
            println(token)
        }
        requestNotificationsPermission()
        checkGoogleApiServiceAvailable()
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }
    fun checkGoogleApiServiceAvailable(){
        val code = googleApiAvailability.isGooglePlayServicesAvailable(this@MainActivity)
        if(code == ConnectionResult.SUCCESS)
            return
        if(googleApiAvailability.isUserResolvableError(code)){
            return
        }
        Toast.makeText(this@MainActivity,"Google Api unavailable",Toast.LENGTH_LONG).show()
    }
    private fun requestNotificationsPermission() {
        if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) {
            return
        }
        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermissions(arrayOf(permission), 1)
    }

}

