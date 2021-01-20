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

class PageViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val isDataLoaded: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private var noteList: List<NoteEntity> = listOf()

    var editableNote: NoteEntity? = null

    init {
        loadNotes()
    }

    fun getNotes(groupId: Int) = getByGroup(groupId)

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

    private fun getByGroup(groupId: Int) =
        when (groupId) {
            1 -> noteList
            else -> noteList.filter { it.groupId == groupId }
        }

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