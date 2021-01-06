package com.markel.passwordreminder.ui.page_fragment.note

data class NoteItemClick(
    val noteId: Int,
    val noteIndex: Int,
    val operationType: OperationType
)

enum class OperationType {
    EDIT,
    SHARE,
    DELETE
}
