package com.financius.features

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.financius.R.string
import com.financius.models.Error
import com.financius.models.UnknownError

class ErrorDisplayer(private val context: Context) {
    constructor(view: View) : this(view.context)
    constructor(fragment: Fragment) : this(fragment.context!!)

    fun showToast(error: Error) {
        val errorTitle = when (error) {
            is UnknownError -> context.getString(string.error_unknown)
        }
        Toast.makeText(context, errorTitle, LENGTH_SHORT).show()
        Log.w("ErrorDisplayer", "Handled error - $errorTitle", error.cause)
    }
}

fun Context.errorShowToast(error: Error) = ErrorDisplayer(this).showToast(error)
fun View.errorShowToast(error: Error) = ErrorDisplayer(this).showToast(error)
fun Fragment.errorShowToast(error: Error) = ErrorDisplayer(this).showToast(error)