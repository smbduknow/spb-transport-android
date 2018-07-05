package me.smbduknow.transport.presentation.misc

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.dismissKeyboard(view: View? = null) {
    try {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        else if (imm.isAcceptingText) {// verify if the soft keyboard is open
            //
            // If no view currently has focus, create a new one, just so we can grab a window token from it
            val focusView = currentFocus ?: View(this)
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
        }
    } catch (e: Exception) {}
}