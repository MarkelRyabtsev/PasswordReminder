package com.markel.passwordreminder.ui.page_fragment.note

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.LayoutRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.adapter.DisplayAdapter
import com.markel.passwordreminder.base.adapter.DisplayItem
import com.markel.passwordreminder.base.adapter.DisplayViewHolder
import com.markel.passwordreminder.base.constants.Constant.HIDED_PASSWORD_DOTS
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.getValueAnimator
import com.markel.passwordreminder.ext.hide
import com.markel.passwordreminder.ext.loadImage
import com.markel.passwordreminder.ext.show
import kotlinx.android.synthetic.main.item_note_list.*

class NoteAdapter : DisplayAdapter() {

    override fun createViewHolder(view: View, @LayoutRes viewType: Int) = when (viewType) {
        R.layout.item_note_list -> NoteDisplay.ViewHolder(view)
        else -> throw RuntimeException("Unknown type $viewType, you should modify createViewHolder")
    }
}

data class NoteDisplay(
    val wrapped: NoteEntity
) : DisplayItem(R.layout.item_note_list) {
    class ViewHolder(
        view: View
    ) : DisplayViewHolder<NoteDisplay>(view) {

        @SuppressLint("ResourceType")
        override fun bind(item: NoteDisplay) {
            note_description.text = item.wrapped.description

            if (item.wrapped.passwordHided) {
                note_password.text = HIDED_PASSWORD_DOTS
                passwordToggle.loadImage(R.drawable.ic_card_eye_opened)
            } else {
                note_password.text = item.wrapped.password
                passwordToggle.loadImage(R.drawable.ic_card_eye_closed)
            }

            if (item.wrapped.isProtected) {
                passwordToggle.show()
                passwordToggle.setOnClickListener {
                    if (!item.wrapped.passwordHided) {
                        passwordToggle.loadImage(R.drawable.ic_card_eye_opened)
                        note_password.text = HIDED_PASSWORD_DOTS
                        item.wrapped.passwordHided = true
                    } else {
                        passwordToggle.loadImage(R.drawable.ic_card_eye_closed)
                        note_password.text = item.wrapped.password
                        item.wrapped.passwordHided = false
                    }
                }
            } else passwordToggle.hide()

            /*card_container.setOnClickListener {
                when {
                    expandedItem == null -> {
                        expandItem(this, expand = true, animate = true)
                        expandedItem = item
                    }
                    expandedItem == item -> {
                        expandItem(this, expand = false, animate = true)
                        expandedItem = null
                    }
                    else -> {
                        *//*val expandedModelPosition = items.indexOf(expandedItem!!)
                        val oldViewHolder =
                            recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ViewHolder
                        if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)*//*

                        expandItem(this, expand = true, animate = true)
                        expandedItem = item
                    }
                }
            }
        }

        private fun expandItem(holder: ViewHolder, expand: Boolean, animate: Boolean) {
            if (animate) {
                val animator = getValueAnimator(
                    expand, 300, AccelerateDecelerateInterpolator()
                ) { progress -> setExpandProgress(holder, progress) }

                if (expand) animator.doOnStart { cl_more.isVisible = true }
                else animator.doOnEnd { cl_more.isVisible = false }

                animator.start()
            } else {

                // show expandView only if we have expandedHeight (onViewAttached)
                //cl_more.isVisible = expand && expandedHeight >= 0
                setExpandProgress(holder, if (expand) 1f else 0f)
            }
        }

        private fun setExpandProgress(holder: ViewHolder, progress: Float) {
            *//*if (expandedHeight > 0 && originalHeight > 0) {
                card_container.layoutParams.height =
                    (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
            }
            card_container.layoutParams.width =
                (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

            card_container.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
            card_container.requestLayout()*//*

            iv_card_arrow.rotation = 90 * progress
        }*/
        }
    }
}