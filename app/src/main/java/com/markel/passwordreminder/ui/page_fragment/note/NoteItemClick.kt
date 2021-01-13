package com.markel.passwordreminder.ui.page_fragment.note

import com.markel.passwordreminder.database.entity.NoteEntity

data class NoteItemClick(
    val note: NoteEntity,
    val noteIndex: Int,
    val operationType: OperationType
)

enum class OperationType {
    EDIT,
    SHARE,
    DELETE
}
