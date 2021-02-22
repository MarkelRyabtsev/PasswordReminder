package com.markel.passwordreminder.ui.dialog

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class FolderActionsViewModel(
    private val groupRepository: GroupRepository,
    private val folderId: Int
) : BaseViewModel() {

    val deleteFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val currentFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    var currentFolder: GroupEntity? = null

    init {
        getFolder(folderId)
    }

    private fun getFolder(folderId: Int) {
        makeRequest({ groupRepository.getFolderById(folderId) }) {
            when (it) {
                is RequestResult.Success -> {
                    currentFolderLiveData.setSuccess(true)
                    currentFolder = it.result
                }
            }
        }
    }

    fun deleteFolder() {
        currentFolder?.let {
            deleteFolderLiveData.setLoading()
            makeRequest({ groupRepository.deleteFolderWithNotes(it) }) {
                when (it) {
                    is RequestResult.Success -> {
                        deleteFolderLiveData.setSuccess(true)
                    }
                    is RequestResult.Error -> deleteFolderLiveData.setError(it.error)
                }
            }
        }
    }
}