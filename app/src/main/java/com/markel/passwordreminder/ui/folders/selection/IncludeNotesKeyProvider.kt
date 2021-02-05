package com.markel.passwordreminder.ui.folders.selection

import androidx.recyclerview.selection.ItemKeyProvider
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ui.folders.adapter.IncludeNotesAdapter

class IncludeNotesKeyProvider(private val adapter: IncludeNotesAdapter) : ItemKeyProvider<NoteEntity>(
    SCOPE_CACHED
) {

    override fun getKey(position: Int) = adapter.adapterList.getOrNull(position)
    override fun getPosition(key: NoteEntity) = adapter.adapterList.indexOf(key)
}