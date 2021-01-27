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

class MainPageViewModel(
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val notes: MutableLiveData<Resource<List<NoteEntity>>> = MutableLiveData()

    init {
        loadNotes()
    }

    fun updateNotes() = loadNotes()

    private fun loadNotes() {
        notes.setLoading()
        makeRequest({ noteRepository.getAllNotes() }) {
            when (it) {
                is RequestResult.Success -> {
                    notes.setSuccess(it.result)
                }
                is RequestResult.Error -> {
                    notes.setError(it.error)
                }
            }
        }
    }
}