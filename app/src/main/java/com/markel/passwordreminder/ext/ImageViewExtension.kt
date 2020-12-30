package com.markel.passwordreminder.ext

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load

fun ImageView.loadImage(@DrawableRes image: Int) = this.load(image)