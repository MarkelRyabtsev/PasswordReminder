package com.markel.passwordreminder.custom

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.markel.passwordreminder.R
import com.markel.passwordreminder.ext.*
import com.markel.passwordreminder.ext.findSuitableParent

class CustomSnackbar(
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(view.context.loadColor(android.R.color.transparent))
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View,
            noteDescription: String,
            duration: Int,
            listener: View.OnClickListener?,
            timerIsDone: () -> Unit
        ): CustomSnackbar? {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.layout_custom_snackbar,
                    parent,
                    false
                ) as CustomSnackbarView
                // We create and return our Snackbar
                customView.apply {
                    tvNoteDescription.text = noteDescription
                    tvActionUndo.setSafeOnClickListener {
                        listener?.onClick(customView.tvActionUndo)
                    }
                    progressBar.apply {
                        max = 100
                        progress = 100
                        setProgressAnimation(duration.toLong()) {
                            timerIsDone.invoke()
                        }
                    }
                }

                return CustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {

            }

            return null
        }
    }
}