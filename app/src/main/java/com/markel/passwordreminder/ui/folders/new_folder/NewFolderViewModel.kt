package com.markel.passwordreminder.ui.folders.new_folder

import androidx.lifecycle.MutableLiveData
import com.markel.passwordreminder.base.ui.BaseViewModel
import com.markel.passwordreminder.base.vo.Resource
import com.markel.passwordreminder.data.RequestResult
import com.markel.passwordreminder.data.repository.GroupRepository
import com.markel.passwordreminder.data.repository.NoteRepository
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setError
import com.markel.passwordreminder.ext.setLoading
import com.markel.passwordreminder.ext.setSuccess

class NewFolderViewModel(
    private val groupRepository: GroupRepository,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val newFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val notesInFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    var currentFoldersNotes: List<NoteEntity> = listOf()

    fun saveFolder(folderName: String, includedNotes: List<NoteEntity>) {
        makeRequest({ groupRepository.addFolderWithNotes(folderName, includedNotes)}) {
            when (it) {
                is RequestResult.Success -> {
                    newFolderLiveData.setSuccess(true)
                }
                is RequestResult.Error -> newFolderLiveData.setError(it.error)
            }
        }
    }

    fun getNotesByIds(noteIds: List<Int>) {
        notesInFolderLiveData.setLoading()
        makeRequest({ noteRepository.getNotesByIds(noteIds) }) {
            when (it) {
                is RequestResult.Success -> {
                    currentFoldersNotes = it.result
                    notesInFolderLiveData.setSuccess(true)
                }
            }
        }
    }
}