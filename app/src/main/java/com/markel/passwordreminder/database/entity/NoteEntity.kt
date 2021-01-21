package com.markel.passwordreminder.database.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val description : String,
    val password : String,
    val isProtected : Boolean = false,
    val groupId : Int? = null
) {
    @Ignore var passwordHided: Boolean = isProtected
    @Ignore var isBackVisible: Boolean = false
}