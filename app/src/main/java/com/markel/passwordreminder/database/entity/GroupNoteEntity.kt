package com.markel.passwordreminder.database.entity

import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(
    tableName = "group_note_junction",
    primaryKeys = ["groupId", "noteId",]
)
data class GroupNoteEntity(
    val groupId: Int,
    val noteId: Int
)
