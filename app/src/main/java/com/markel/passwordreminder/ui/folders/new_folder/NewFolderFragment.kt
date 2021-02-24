package com.markel.passwordreminder.ui.folders.new_folder

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ext.toast
import com.markel.passwordreminder.ui.folders.include_notes.IncludeNotesFragment.Companion.NEW_CHECKED_NOTES
import com.markel.passwordreminder.ui.folders.adapter.SelectedNotesAdapter
import com.markel.passwordreminder.ui.folders.folder_list.FolderListFragment.Companion.NEED_UPDATE_FOLDERS
import kotlinx.android.synthetic.main.fragment_folders_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewFolderFragment : Fragment() {

    private val viewModel: NewFolderViewModel by viewModel()

    private lateinit var adapter: SelectedNotesAdapter
    private lateinit var buttonSave: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    checkFolderChanges()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_folders_new, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeIncludedNotes()
        observeCreatingFolder()

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
                viewModel.saveFolder(
                    et_folder_name.text.toString(),
                    getIncludedNotes()
                )
                return true
            }
        }

        return false
    }

    private fun observeIncludedNotes() {
        val observableData = findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<List<Int>>(NEW_CHECKED_NOTES)
        observableData?.let {
            observe(it) { noteIds ->
                setData(viewModel.getNotesById(noteIds))
            }
        }
    }

    private fun observeCreatingFolder() {
        observe(viewModel.newFolderLiveData) {
            when (it.status) {
                Status.SUCCESS -> {
                    findNavController().apply {
                        previousBackStackEntry?.savedStateHandle?.set(NEED_UPDATE_FOLDERS, it.data)
                        navigateUp()
                    }
                }
                Status.ERROR -> requireActivity().toast(it.message)
            }
        }
    }

    private fun initAdapter() {
        adapter = SelectedNotesAdapter(requireActivity(), ::itemListRemoved)
        rv_included_notes.adapter = adapter
    }

    private fun initListeners() {
        ll_add_notes.setSafeOnClickListener {
            findNavController().navigate(
                NewFolderFragmentDirections
                    .actionNewFolderFragmentToAddNotesFragment(
                        getIncludedNotesIds().toIntArray()
                    )
            )
        }
    }

    private fun initButtonSaveVisibilityListener() {
        et_folder_name.doAfterTextChanged {
            setButtonSaveVisibility()
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

    private fun itemListRemoved() {
        /*viewModel.changedNoteList = adapter.getList()
        checkChanges()*/
    }

    private fun checkFolderChanges() {
        if (isHasChanges()) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Create Folder?")
                setMessage("Вы еще на завершили создание Папки. Создать сейчас?")
                setPositiveButton(R.string.action_create) { dialog, _ ->
                    viewModel.saveFolder(
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

    private fun setButtonSaveVisibility() {
        buttonSave.isVisible = isHasChanges()
    }

    private fun isHasChanges() = et_folder_name.text.isNotEmpty()
}