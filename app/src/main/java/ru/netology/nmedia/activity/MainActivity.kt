package ru.netology.nmedia.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewModel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
        }
        requestNotificationsPermission()
        checkGoogleApiServiceAvailable()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        mainActivityBinding.bottomNavigation.setupWithNavController(navHostFragment.navController)
        /*addMenuProvider(object : MenuProvider {
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

         */
        setContentView(mainActivityBinding.root)
    }

    fun checkGoogleApiServiceAvailable() {
        val code = googleApiAvailability.isGooglePlayServicesAvailable(this@MainActivity)
        if (code == ConnectionResult.SUCCESS)
            return
        if (googleApiAvailability.isUserResolvableError(code)) {
            return
        }
        Toast.makeText(this@MainActivity, "Google Api unavailable", Toast.LENGTH_LONG).show()
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

