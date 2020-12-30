package com.markel.passwordreminder.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.markel.passwordreminder.R

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {
    private var view: View? = null

    init {
        view = View.inflate(context, R.layout.custom_view_snackbar, this)
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

    override fun animateContentIn(delay: Int, duration: Int) {
    }
}