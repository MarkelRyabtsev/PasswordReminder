package com.markel.passwordreminder.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.markel.passwordreminder.R
import com.markel.passwordreminder.ext.findSuitableParent
import com.markel.passwordreminder.ext.loadColor

class CustomSnackbar(
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(view.context.loadColor(android.R.color.transparent))
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(view: View): CustomSnackbar {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.layout_custom_snackbar,
                parent,
                false
            ) as CustomSnackbarView

            // We create and return our Snackbar
            return CustomSnackbar(
                parent,
                customView
            )
        }

    }
}