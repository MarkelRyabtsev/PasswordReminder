package com.markel.passwordreminder.ext

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> FragmentActivity.observe(liveData: LiveData<T>?, block: (T) -> Unit) {
    liveData?.observe(this, Observer(block))
}