package com.markel.passwordreminder.ui.folders.edit_folder

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.dialog.FolderActionsDialogArgs
import com.markel.passwordreminder.ui.folders.include_notes.IncludeNotesFragment
import com.markel.passwordreminder.ui.folders.adapter.SelectedNotesAdapter
import kotlinx.android.synthetic.main.fragment_folders_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditFolderFragment : Fragment() {

    private val viewModel: EditFolderViewModel by viewModel {
        parametersOf(getFolderId())
    }

    private lateinit var adapter: SelectedNotesAdapter
    private lateinit var buttonSave: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_folders_new, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeFolder()
        observeFoldersNotes()
        observeSavingChanges()

        initAdapter()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.activity_folders_menu, menu)
        buttonSave = menu.findItem(R.id.action_save)

        initButtonSaveVisibilityListener()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                viewModel.saveChanges(
                    et_folder_name.text.toString(),
                    getIncludedNotes()
                )
                return true
            }
        }

        return false
    }

    private fun initEditTextFolderName() {
        et_folder_name.setText(viewModel.currentFolder?.name ?: getFolderName())
    }

    private fun observeFolder() {
        observe(viewModel.currentFolderLiveData) {
            when (it.status) {
                Status.SUCCESS -> initEditTextFolderName()
            }
        }
    }

    private fun observeFoldersNotes() {
        observe(viewModel.notesInFolderLiveData) {
            when (it.status) {
                Status.SUCCESS -> setData(viewModel.currentFoldersNotes)
            }
        }
    }

    private fun observeSavingChanges() {
        observe(viewModel.saveChangesLiveData) {
            when (it.status) {
                Status.SUCCESS -> {
                    //viewModel.updateGroupList()
                }
                Status.ERROR -> requireActivity().toast(it.message)
            }

            findNavController().navigateUp()
        }
    }

    private fun initAdapter() {
        adapter = SelectedNotesAdapter(requireActivity())
        rv_included_notes.adapter = adapter
    }

    private fun initListeners() {
        ll_add_notes.setSafeOnClickListener {
            findNavController().navigate(
                EditFolderFragmentDirections
                    .actionEditFolderFragmentToAddNotesToExistingFolderFragment(
                        getIncludedNotesIds().toIntArray()
                    )
            )
        }
    }

    private fun initButtonSaveVisibilityListener() {
        et_folder_name.doAfterTextChanged {
            viewModel.changedFolderName = it.toString()
            checkChanges()
        }
    }

    private fun setData(noteList: List<NoteEntity>?) {
        noteList?.let {
            adapter.setList(it)
        }
    }

    private fun getIncludedNotes(): List<NoteEntity> {
        return adapter.adapterList
    }

    private fun getIncludedNotesIds(): List<Int> {
        return getIncludedNotes().map { it.id }
    }

    private fun checkFolderChanges() {
        if (et_folder_name.text.isNotEmpty()) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Create Folder?")
                setMessage("Вы еще на завершили создание Папки. Создать сейчас?")
                setPositiveButton(R.string.action_create) { dialog, _ ->
                    viewModel.saveChanges(
                        et_folder_name.text.toString(),
                        getIncludedNotes()
                    )
                    dialog.dismiss()
                }
                setNegativeButton(R.string.action_discard) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                show()
            }
        } else findNavController().navigateUp()
    }

    private fun checkChanges() {
        buttonSave.isVisible = !(viewModel.originalFolderName == viewModel.changedFolderName
                && viewModel.originalNoteList == viewModel.changedNoteList)
    }

    private fun getFolderId(): Int {
        val args: EditFolderFragmentArgs by navArgs()
        return args.folderId
    }

    private fun getFolderName(): String {
        val args: EditFolderFragmentArgs by navArgs()
        return args.title
    }
}