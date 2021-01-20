package com.markel.passwordreminder.ui.main

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class MainViewModel(
    private val groupRepository: GroupRepository,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val groupListLiveData: MutableLiveData<Resource<List<GroupEntity>>> = MutableLiveData()

    init {
        getGroups()
    }

    fun addGroup(group: GroupEntity) {
        makeRequest({ groupRepository.addGroup(group) }) {
            when (it) {
                is RequestResult.Success -> {
                    updateGroupList()
                }
            }
        }
    }

    fun addNote(newNote: NoteEntity) {
        makeRequest({ noteRepository.addNote(newNote) }) {
            when (it) {
                is RequestResult.Success -> {
                    updateGroupList()
                }
            }
        }
    }

    fun editNote(editedNote: NoteEntity) {
        makeRequest({ noteRepository.editNote(editedNote) }) {
            when (it) {
                is RequestResult.Success -> {
                    updateGroupList()
                }
            }
        }
    }

    private fun getGroups() {
        groupListLiveData.setLoading()
        makeRequest({ groupRepository.getGroups() }) {
            when (it) {
                is RequestResult.Success -> {
                    groupListLiveData.setSuccess(it.result)
                }
                is RequestResult.Error -> {
                    groupListLiveData.setError(it.error)
                }
            }
        }
    }

    fun updateGroupList() {
        getGroups()
    }
}