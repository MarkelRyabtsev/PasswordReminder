package com.markel.passwordreminder.ui.page_fragment.view_model

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class EditNoteViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    var editableNote: NoteEntity? = null
    val updateNote = MutableLiveData<Resource<Boolean>>()
    val editNote = MutableLiveData<Resource<Boolean>>()
    val addNote = MutableLiveData<Resource<Boolean>>()

    fun updateNotes() {
        updateNote.setSuccess(true)
    }

    fun deleteNote(noteId: Int) {
        makeRequest({
            noteRepository.deleteNote(noteId)
        }
        ) {
            when (it) {
                is RequestResult.Success -> {
                    updateNote.setSuccess(true)
                }
                is RequestResult.Error -> {
                    updateNote.setError(it.error)
                }
            }
        }
    }

    fun addNote(newNote: NoteEntity) {
        addNote.setLoading()
        makeRequest({ noteRepository.addNote(newNote) }) {
            when (it) {
                is RequestResult.Success -> {
                    addNote.setSuccess(true)
                }
                is RequestResult.Error -> {
                    addNote.setError(it.error)
                }
            }
        }
    }

    fun editNote(editedNote: NoteEntity) {
        editNote.setLoading()
        makeRequest({ noteRepository.editNote(editedNote) }) {
            when (it) {
                is RequestResult.Success -> {
                    editNote.setSuccess(true)
                }
                is RequestResult.Error -> {
                    editNote.setError(it.error)
                }
            }
        }
    }
}