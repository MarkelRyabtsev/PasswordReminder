package com.markel.passwordreminder.ui.page_fragment.note

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import com.markel.passwordreminder.database.entity.NoteEntity

interface ViewHolderWithDetails<TItem> {
    fun getItemDetail(): ItemDetailsLookup.ItemDetails<TItem>
}

class NoteItemDetails(private val adapterPosition: Int, private val selectedKey: NoteEntity?) :
    ItemDetailsLookup.ItemDetails<NoteEntity>() {

    override fun getSelectionKey() = selectedKey
    override fun getPosition() = adapterPosition
}

class IncludeNoteItemDetails(private val adapterPosition: Int, private val selectedKey: NoteEntity?) :
    ItemDetailsLookup.ItemDetails<NoteEntity>() {

    override fun getSelectionKey() = selectedKey
    override fun getPosition() = adapterPosition
    override fun inSelectionHotspot(e: MotionEvent): Boolean = true
}