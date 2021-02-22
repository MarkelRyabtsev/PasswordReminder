package com.markel.passwordreminder.ui.folders.new_folder

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
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
import com.markel.passwordreminder.ui.folders.include_notes.IncludeNotesFragment.Companion.NEW_CHECKED_NOTES
import com.markel.passwordreminder.ui.folders.adapter.SelectedNotesAdapter
import com.markel.passwordreminder.ui.folders.folder_list.FolderListFragment.Companion.NEED_UPDATE_FOLDERS
import com.markel.passwordreminder.ui.folders.include_notes.IncludeNotesFragment
import kotlinx.android.synthetic.main.fragment_folders_new.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewFolderFragment : Fragment() {

    private val viewModel: NewFolderViewModel by viewModel()

    private lateinit var adapter: SelectedNotesAdapter
    private lateinit var buttonSave: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            checkFolderChanges()
        }
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
        setButtonSaveVisibility(!et_folder_name.text.isNullOrEmpty())
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

    private fun setButtonSaveVisibility(isVisible: Boolean) {
        buttonSave.isVisible = isVisible
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
                    updateFolders(it.data ?: false)
                }
                Status.ERROR -> requireActivity().toast(it.message)
            }
        }
    }

    private fun initAdapter() {
        adapter = SelectedNotesAdapter(requireActivity())
        rv_included_notes.adapter = adapter
    }

    private fun initListeners() {
        ll_add_notes.setSafeOnClickListener {
            findNavController().navigate(
                R.id.action_newFolderFragment_to_addNotesFragment,
                bundleOf(IncludeNotesFragment.PASSED_CHECKED_NOTES to adapter.adapterList.map { it.id })
            )
        }
    }

    private fun initButtonSaveVisibilityListener() {
        et_folder_name.doAfterTextChanged {
            setButtonSaveVisibility(!it.isNullOrEmpty())
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

    private fun getIncludedNoteIds() : List<Int>? {
        val args: NewFolderFragmentArgs by navArgs()
        return args.includedNoteIds?.toList()
    }

    private fun updateFolders(needUpdate: Boolean) {
        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(NEED_UPDATE_FOLDERS, needUpdate)
            navigateUp()
        }
    }

    private fun checkFolderChanges() {
        if (et_folder_name.text.isNotEmpty()) {
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
                    updateFolders(false)
                    dialog.dismiss()
                }
                show()
            }
        } else updateFolders(false)
    }
}