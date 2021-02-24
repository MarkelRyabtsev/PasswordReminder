package com.markel.passwordreminder.database.dao

import androidx.room.*
import com.markel.passwordreminder.database.entity.NoteEntity

@Dao
interface NoteDao {
    @Insert
    fun addNote(note: NoteEntity)

    @Update
    fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Int)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id IN (:noteIds)")
    fun getNotesByIds(noteIds: List<Int>): List<NoteEntity>
}