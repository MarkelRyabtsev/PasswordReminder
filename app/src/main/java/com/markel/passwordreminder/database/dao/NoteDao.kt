package com.markel.passwordreminder.database.dao

import androidx.room.*
import com.markel.passwordreminder.database.entity.NoteEntity

@Dao
interface NoteDao {
    @Insert
    fun addNote(note: NoteEntity)

    @Update
    fun updateNote(note: NoteEntity)

    @Delete
    fun deleteNote(note: NoteEntity?)

    @Query("SELECT * FROM notes")
    fun getAllNotes() : List<NoteEntity>

    @Query("SELECT * FROM notes WHERE groupId = :groupId")
    fun getByGroup(groupId: Int) : List<NoteEntity>
}