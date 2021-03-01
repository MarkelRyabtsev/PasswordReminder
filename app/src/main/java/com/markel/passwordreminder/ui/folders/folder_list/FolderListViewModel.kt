package com.markel.passwordreminder.ui.folders.folder_list

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class FolderListViewModel(
    private val groupRepository: GroupRepository
) : BaseViewModel() {

    val groupListLiveData: MutableLiveData<Resource<List<GroupEntity>>> = MutableLiveData()
    val foldersPositionsLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    var originalFolderList: List<GroupEntity> = listOf()
    private var isOriginalFolderListUpdated = false

    init {
        getGroups()
    }

    fun updateGroupList() {
        getGroups()
    }

    private fun getGroups() {
        groupListLiveData.setLoading()
        makeRequest({ groupRepository.getGroups() }) {
            when (it) {
                is RequestResult.Success -> {
                    groupListLiveData.setSuccess(it.result)
                    if (!isOriginalFolderListUpdated) {
                        originalFolderList = it.result
                        isOriginalFolderListUpdated = true
                    }
                }
                is RequestResult.Error -> {
                    groupListLiveData.setError(it.error)
                }
            }
        }
    }

    fun updateFoldersPositions(listIds: List<Int>) {
        makeRequest({  groupRepository.updatePositions(listIds) }) {
            when (it) {
                is RequestResult.Success -> {
                    foldersPositionsLiveData.setSuccess(true)
                }
                is RequestResult.Error -> {
                    foldersPositionsLiveData.setError(it.error)
                }
            }
        }
    }
}