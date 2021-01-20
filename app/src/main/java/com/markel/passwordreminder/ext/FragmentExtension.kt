package com.markel.passwordreminder.ext

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> Fragment.observe(liveData: LiveData<T>, block: (T) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(block))
}

fun Fragment.toast(message: String?, long: Boolean = false) {
    Toast.makeText(
        context ?: return,
        message ?: return,
        if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}