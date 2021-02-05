package com.markel.passwordreminder.ui.folders.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ui.page_fragment.note.ViewHolderWithDetails

class IncludeNotesLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<NoteEntity>() {
    override fun getItemDetails(event: MotionEvent) =
        recyclerView.findChildViewUnder(event.x, event.y)
            ?.let {
                (recyclerView.getChildViewHolder(it) as? ViewHolderWithDetails<NoteEntity>)?.getItemDetail()
            }
}