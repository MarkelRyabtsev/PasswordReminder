package com.markel.passwordreminder.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.loadColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.toast(
    message: CharSequence?,
    isLong: Boolean = false
) = Toast.makeText(
    this,
    message,
    if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
).show()