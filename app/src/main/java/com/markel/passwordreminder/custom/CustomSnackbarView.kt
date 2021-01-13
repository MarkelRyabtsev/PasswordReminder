package com.markel.passwordreminder.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.markel.passwordreminder.R

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    lateinit var tvNoteDescription: TextView
    lateinit var tvActionUndo: TextView
    lateinit var progressBar: ProgressBar

    init {
        View.inflate(context, R.layout.custom_view_snackbar, this)
        clipToPadding = false
        this.tvNoteDescription = findViewById(R.id.tv_description)
        this.tvActionUndo = findViewById(R.id.button_undo)
        this.progressBar = findViewById(R.id.snackBar_progress)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }
}