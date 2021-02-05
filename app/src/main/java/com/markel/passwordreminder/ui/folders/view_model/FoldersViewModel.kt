package com.markel.passwordreminder.ui.folders.view_model

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

class FoldersViewModel(
    private val groupRepository: GroupRepository,
    private val noteRepository: NoteRepository
) : BaseViewModel() {

    val groupListLiveData: MutableLiveData<Resource<List<GroupEntity>>> = MutableLiveData()
    val noteListLiveData: MutableLiveData<Resource<List<NoteEntity>>> = MutableLiveData()
    val notesInFolderLiveData: MutableLiveData<Resource<List<NoteEntity>>> = MutableLiveData()
    val newFolderLiveData: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val notesLoaded: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    private var noteList: List<NoteEntity> = listOf()

    init {
        getGroups()
        getNotes()
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

    fun saveFolder(folderName: String, includedNotes: List<NoteEntity>) {
        makeRequest({ groupRepository.saveFolder(folderName, includedNotes)}) {
            when (it) {
                is RequestResult.Success -> newFolderLiveData.setSuccess(true)
                is RequestResult.Error -> newFolderLiveData.setError(it.error)
            }
        }
    }

    fun getNotesByFolderId(folderId: Int) {
        notesInFolderLiveData.setLoading()
        makeRequest({ noteRepository.getNotesByGroup(folderId) }) {
            when (it) {
                is RequestResult.Success -> {
                    notesInFolderLiveData.setSuccess(it.result)
                }
                is RequestResult.Error -> {
                    notesInFolderLiveData.setError(it.error)
                }
            }
        }
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
                    notesLoaded.setSuccess(true)
                }
                is RequestResult.Error -> {
                    noteListLiveData.setError(it.error)
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

    fun updateNoteList() {
        getNotes()
    }
}