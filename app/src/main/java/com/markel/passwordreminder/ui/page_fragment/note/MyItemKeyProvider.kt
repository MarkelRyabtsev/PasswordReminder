package com.markel.passwordreminder.ui.page_fragment.note

import androidx.recyclerview.selection.ItemKeyProvider
import com.markel.passwordreminder.database.entity.NoteEntity

class MyItemKeyProvider(private val adapter: NoteAdapter) : ItemKeyProvider<NoteEntity>(SCOPE_CACHED) {

    override fun getKey(position: Int) = adapter.adapterList.getOrNull(position)
    override fun getPosition(key: NoteEntity) = adapter.adapterList.indexOf(key)
}