package com.markel.passwordreminder.ui.page_fragment

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess
import com.markel.passwordreminder.ui.page_fragment.note.NoteDisplay

class PageViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val isDataLoaded: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    private var noteList: List<NoteDisplay> = listOf()

    init {
        loadNotes()
    }

    fun getNotes(groupId: Int) = getByGroup(groupId)

    fun updateNotes() = loadNotes()

    private fun getByGroup(groupId: Int) =
        when (groupId) {
            1 -> noteList
            else -> noteList.filter { it.wrapped.groupId == groupId }
        }

    private fun loadNotes() {
        isDataLoaded.setLoading()
        makeRequest({ noteRepository.getAllNotes() }) {
            when (it) {
                is RequestResult.Success -> {
                    noteList = it.result.map { mapNotes(it) }
                    isDataLoaded.setSuccess(true)
                }
                is RequestResult.Error -> {
                    isDataLoaded.setError(it.error)
                }
            }
        }
    }
}