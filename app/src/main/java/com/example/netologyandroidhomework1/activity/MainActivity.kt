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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import com.example.netologyandroidhomework1.NewPostActivityContract
import com.example.netologyandroidhomework1.OnButtonTouchListener
import com.example.netologyandroidhomework1.R
import com.example.netologyandroidhomework1.adapter.PostAdapter
import com.example.netologyandroidhomework1.auth.AppAuth
import com.example.netologyandroidhomework1.databinding.ActivityMainBinding
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.viewModel.AuthViewModel
import com.example.netologyandroidhomework1.viewModel.PostViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    @Inject
    lateinit var auth: AppAuth
    private val viewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.data.observe(this) {
            invalidateOptionsMenu()
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
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.signin -> {
                        // TODO: just hardcode it, implementation must be in homework
                        auth.setAuth(5, "x-token")
                        true
                    }

                    R.id.signup -> {
                        // TODO: just hardcode it, implementation must be in homework
                        auth.setAuth(5, "x-token")
                        true
                    }

                    R.id.signout -> {
                        // TODO: just hardcode it, implementation must be in homework
                        auth.removeAuth()
                        true
                    }

                    else -> false
                }

        })
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

