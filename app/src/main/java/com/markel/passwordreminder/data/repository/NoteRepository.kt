package com.markel.passwordreminder.data.repository

import com.markel.passwordreminder.data.ApiCaller
import com.markel.passwordreminder.data.CoroutineCaller
import com.markel.passwordreminder.database.dao.JunctionDao
import com.markel.passwordreminder.database.dao.NoteDao
import com.markel.passwordreminder.database.entity.NoteEntity
import java.lang.Exception

class NoteRepository(
    private val noteDao: NoteDao,
    private val junctionDao: JunctionDao
) : CoroutineCaller by ApiCaller {

    private var cache: List<NoteEntity>? = null

    suspend fun getAllNotes(getFromDb: Boolean = false) = apiCall {
        val notes = if (getFromDb) noteDao.getAllNotes().also {
            cache = it
        } ?: throw Exception("empty data")
        else cache ?: noteDao.getAllNotes()

        notes.sortedBy { it.id }
    }

    suspend fun addNote(newNote: NoteEntity) = apiCall {
        noteDao.addNote(newNote)
    }

    suspend fun editNote(editedNote: NoteEntity) = apiCall {
        noteDao.updateNote(editedNote)
    }

    suspend fun getNotesByGroup(groupId: Int) = apiCall {
        junctionDao.getNotesByGroup(groupId)
    }

    suspend fun deleteNote(noteId: Int) = apiCall {
        noteDao.deleteNoteById(noteId)
    }

    suspend fun getNotesByIds(noteIds: List<Int>) = apiCall {
        noteDao.getNotesByIds(noteIds)
    }
}