package com.markel.passwordreminder.ui.page_fragment.note

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.constants.Constant
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.*
import com.markel.passwordreminder.util.NotesDiffUtil
import com.markel.passwordreminder.util.bindColor
import com.markel.passwordreminder.util.bindDimen
import com.markel.passwordreminder.util.bindView

class NoteAdapter(
    context: Context,
    private val clickListener: (NoteItemClick) -> Unit
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private val originalBg: Int by bindColor(context, R.color.list_item_bg_collapsed)
    private val expandedBg: Int by bindColor(context, R.color.list_item_bg_expanded)

    private val listItemHorizontalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_horizontal_padding
    )
    private val listItemVerticalPadding: Float by bindDimen(
        context,
        R.dimen.list_item_horizontal_padding
    )
    private val originalWidth = context.screenWidth - 24.dp
    private val expandedWidth = context.screenWidth - 8.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private var adapterList = ArrayList<NoteEntity>()

    private val listItemExpandDuration: Long get() = 300L
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: NoteEntity? = null
    private var isScaledDown = false

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    fun setList(list: List<NoteEntity>) {
        DiffUtil.calculateDiff(
            NotesDiffUtil(adapterList, list)
        ).dispatchUpdatesTo(this)
        adapterList.clear()
        adapterList.addAll(list)
    }

    override fun getItemCount(): Int = adapterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.item_note_list, parent, false))

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = adapterList[position]

        holder.noteDescription.text = model.description
        if (model.passwordHided) {
            holder.notePassword.text = Constant.HIDED_PASSWORD_DOTS
            holder.passwordToggle.loadImage(R.drawable.ic_card_eye_opened)
        } else {
            holder.notePassword.text = model.password
            holder.passwordToggle.loadImage(R.drawable.ic_card_eye_closed)
        }

        if (model.isProtected) {
            holder.passwordToggle.show()
            holder.passwordToggle.setSafeOnClickListener {
                if (!model.passwordHided) {
                    holder.passwordToggle.loadImage(R.drawable.ic_card_eye_opened)
                    holder.notePassword.text = Constant.HIDED_PASSWORD_DOTS
                    model.passwordHided = true
                } else {
                    holder.passwordToggle.loadImage(R.drawable.ic_card_eye_closed)
                    holder.notePassword.text = model.password
                    model.passwordHided = false
                }
            }
        } else holder.passwordToggle.hide()

        holder.editButton.setSafeOnClickListener {
            clickListener(
                NoteItemClick(
                    model.id,
                    position,
                    OperationType.EDIT
                )
            )
        }
        holder.shareButton.setSafeOnClickListener {
            clickListener(
                NoteItemClick(
                    model.id,
                    position,
                    OperationType.SHARE
                )
            )
        }
        holder.deleteButton.setSafeOnClickListener {
            clickListener(
                NoteItemClick(
                    model.id,
                    position,
                    OperationType.DELETE
                )
            )
        }

        expandItem(holder, model == expandedModel, animate = false)
        scaleDownItem(holder, position, isScaledDown)

        holder.cardContainer.setOnClickListener {
            if (expandedModel == null) {

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            } else if (expandedModel == model) {

                // collapse clicked view
                expandItem(holder, expand = false, animate = true)
                expandedModel = null
            } else {

                // collapse previously expanded view
                val expandedModelPosition = adapterList.indexOf(expandedModel!!)
                val oldViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ViewHolder
                if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            }
        }
    }

    private fun expandItem(holder: ViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            val animator = getValueAnimator(
                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.expandView.isVisible = true }
            else animator.doOnEnd { holder.expandView.isVisible = false }

            animator.start()
        } else {

            // show expandView only if we have expandedHeight (onViewAttached)
            holder.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        // get originalHeight & expandedHeight if not gotten before
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.expandView.isVisible = true
                view.doOnPreDraw {
                    expandedHeight = view.height
                    holder.expandView.isVisible = false
                }
            }
        }
    }

    private fun setExpandProgress(holder: ViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.cardContainer.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.cardContainer.layoutParams.width =
            (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        holder.cardContainer.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
        holder.cardContainer.requestLayout()

        holder.chevron.rotation = 90 * progress
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scale Down Animation
    ///////////////////////////////////////////////////////////////////////////

    private fun setScaleDownProgress(holder: ViewHolder, position: Int, progress: Float) {
        val itemExpanded = position >= 0 && adapterList[position] == expandedModel
        holder.cardContainer.layoutParams.apply {
            width =
                ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height =
                ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
        }
        holder.cardContainer.requestLayout()

        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.scaleContainer.setPadding(
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

        holder.listItemFg.alpha = progress
    }

    /** Convenience method for calling from onBindViewHolder */
    private fun scaleDownItem(holder: ViewHolder, position: Int, isScaleDown: Boolean) {
        setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteDescription: TextView by bindView(R.id.note_description)
        val notePassword: TextView by bindView(R.id.note_password)
        val passwordToggle: ImageView by bindView(R.id.passwordToggle)

        val editButton: ImageView by bindView(R.id.iv_card_edit)
        val shareButton: ImageView by bindView(R.id.iv_card_share)
        val deleteButton: ImageView by bindView(R.id.iv_card_delete)

        val expandView: View by bindView(R.id.cl_more)
        val chevron: View by bindView(R.id.iv_card_arrow)
        val cardContainer: View by bindView(R.id.card_container)
        val scaleContainer: View by bindView(R.id.cl_front)
        val listItemFg: View by bindView(R.id.list_item_fg)
    }
}