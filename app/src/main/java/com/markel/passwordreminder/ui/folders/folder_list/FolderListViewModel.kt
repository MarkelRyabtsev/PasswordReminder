package com.markel.passwordreminder.ui.folders.folder_list

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class FolderListViewModel(
    private val groupRepository: GroupRepository
) : BaseViewModel() {

    val groupListLiveData: MutableLiveData<Resource<List<GroupEntity>>> = MutableLiveData()

    init {
        getGroups()
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