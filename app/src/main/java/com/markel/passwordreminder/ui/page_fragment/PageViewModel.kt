package com.markel.passwordreminder.ui.page_fragment

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess
import timber.log.Timber

class PageViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val isDataLoaded: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private var noteList: List<NoteEntity> = listOf()

    var editableNote: NoteEntity? = null
    val updateNote = MutableLiveData<Resource<Boolean>>()
    val addNote = MutableLiveData<Resource<Boolean>>()

    fun getNotesByGroupId(groupId: Int) = getByGroup(groupId)

    fun getAllNotes() = noteList

    fun updateNotes() = loadNotes()

    fun deleteNote(id: Int) {
        makeRequest({ noteRepository.deleteNote(
            noteList.firstOrNull {it.id == id})}
        ) {
            when (it) {
                is RequestResult.Success -> { loadNotes() }
                is RequestResult.Error -> {}
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
        updateNote.setLoading()
        makeRequest({ noteRepository.editNote(editedNote) }) {
            when (it) {
                is RequestResult.Success -> {
                    updateNote.setSuccess(true)
                    loadNotes()
                }
                is RequestResult.Error -> {
                    updateNote.setError(it.error)
                }
            }
        }
    }

    private fun getByGroup(groupId: Int) = noteList.filter { it.groupId == groupId }

    private fun loadNotes() {
        isDataLoaded.setLoading()
        makeRequest({ noteRepository.getAllNotes() }) {
            when (it) {
                is RequestResult.Success -> {
                    noteList = it.result
                    isDataLoaded.setSuccess(true)
                }
                is RequestResult.Error -> {
                    isDataLoaded.setError(it.error)
                }
            }
        }
    }
}