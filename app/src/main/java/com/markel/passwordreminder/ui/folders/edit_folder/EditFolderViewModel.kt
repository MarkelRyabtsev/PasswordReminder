package com.markel.passwordreminder.ui.folders.edit_folder

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

class EditFolderViewModel(
    private val groupRepository: GroupRepository,
    private val noteRepository: NoteRepository,
    private val folderId: Int
) : BaseViewModel() {

    val saveChangesLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val currentFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val notesInFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    var currentFolder: GroupEntity? = null
    var currentFoldersNotes: List<NoteEntity> = listOf()

    var originalFolderName: String = ""
    var originalNoteList: List<NoteEntity> = listOf()
    var changedFolderName: String = ""
    var changedNoteList: List<NoteEntity> = listOf()

    init {
        getFolder(folderId)
    }

    fun saveChanges(folderName: String, includedNotes: List<NoteEntity>) {
        makeRequest({ groupRepository.saveFolderChanges(folderId, folderName, includedNotes)}) {
            when (it) {
                is RequestResult.Success -> {
                    saveChangesLiveData.setSuccess(true)
                }
                is RequestResult.Error -> saveChangesLiveData.setError(it.error)
            }
        }
    }

    private fun getNotesByFolderId(folderId: Int) {
        notesInFolderLiveData.setLoading()
        makeRequest({ noteRepository.getNotesByGroup(folderId) }) {
            when (it) {
                is RequestResult.Success -> {
                    currentFoldersNotes = it.result
                    notesInFolderLiveData.setSuccess(true)

                    originalNoteList = it.result
                    changedNoteList = it.result
                }
            }
        }
    }

    private fun getFolder(folderId: Int) {
        makeRequest({ groupRepository.getFolderById(folderId) }) {
            when (it) {
                is RequestResult.Success -> {
                    currentFolder = it.result
                    currentFolderLiveData.setSuccess(true)
                    getNotesByFolderId(folderId)

                    it.result?.let { folder ->
                        originalFolderName = folder.name
                        changedFolderName = folder.name
                    }
                }
            }
        }
    }

    fun getNotesByIds(noteIds: List<Int>) {
        notesInFolderLiveData.setLoading()
        makeRequest({ noteRepository.getNotesByIds(noteIds) }) {
            when (it) {
                is RequestResult.Success -> {
                    currentFoldersNotes = it.result
                    changedNoteList = it.result
                    notesInFolderLiveData.setSuccess(true)
                }
            }
        }
    }
}