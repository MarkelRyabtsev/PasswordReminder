package com.markel.passwordreminder.ext

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.custom.SafeClickListener

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun showGroupViews(vararg view: View) {
    view.forEach {
        it.show()
    }
}

fun hideGroupViews(vararg view: View) {
    view.forEach {
        it.hide()
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun <T> MutableLiveData<Resource<T>>.setSuccess(data: T?) =
    postValue(Resource.success(data))

fun <T> MutableLiveData<Resource<T>>.setLoading() =
    postValue(Resource.loading())

fun <T> MutableLiveData<Resource<T>>.setError(e: Throwable) =
    postValue(Resource.error(e.message, e))

fun <T> MutableLiveData<Resource<T>>.setError(s: String) =
    postValue(Resource.error(s))