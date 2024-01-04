package com.example.netologyandroidhomework1

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import retrofit2.http.Url
import java.net.MalformedURLException
import java.net.URL

class AndroidUtils {
    companion object {
        fun hideKeyBoard(view: View) {
            (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyBoard(view: View) {
            val imm =  (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            if (view.requestFocus())
                view.postDelayed(Runnable {
                    view.requestFocus()
                    imm.showSoftInput(view,0)
                },100
                )
        }
    }
}