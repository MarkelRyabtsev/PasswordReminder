package com.markel.passwordreminder.ui.page_fragment.note

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.database.entity.NoteEntity

class MyItemLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<NoteEntity>() {
    override fun getItemDetails(event: MotionEvent) =
        recyclerView.findChildViewUnder(event.x, event.y)
            ?.let {
                (recyclerView.getChildViewHolder(it) as? ViewHolderWithDetails<NoteEntity>)?.getItemDetail()
            }
}