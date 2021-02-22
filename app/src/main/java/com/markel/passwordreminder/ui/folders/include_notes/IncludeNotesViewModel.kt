package com.markel.passwordreminder.ui.folders.include_notes

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class IncludeNotesViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val noteListLiveData: MutableLiveData<Resource<List<NoteEntity>>> = MutableLiveData()

    private var noteList: List<NoteEntity> = listOf()

    init {
        getNotes()
    }

    fun getNotesById(noteIds: List<Int>?) : List<NoteEntity>? {
        return noteIds?.let {
            noteList.filter { note -> note.id in noteIds }
        }
    }

    private fun getNotes() {
        noteListLiveData.setLoading()
        makeRequest({ noteRepository.getAllNotes() }) {
            when (it) {
                is RequestResult.Success -> {
                    noteListLiveData.setSuccess(it.result)
                    noteList = it.result
                }
                is RequestResult.Error -> {
                    noteListLiveData.setError(it.error)
                }
            }
        }
    }
}