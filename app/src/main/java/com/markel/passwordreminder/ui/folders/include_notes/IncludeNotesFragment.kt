package com.markel.passwordreminder.ui.folders.include_notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.google.android.material.chip.Chip
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ui.folders.adapter.IncludeNotesAdapter
import com.markel.passwordreminder.ui.folders.selection.IncludeNotesKeyProvider
import com.markel.passwordreminder.ui.folders.selection.IncludeNotesLookup
import kotlinx.android.synthetic.main.fragment_folders_include_notes.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class IncludeNotesFragment : Fragment() {

    private val viewModel: IncludeNotesViewModel by viewModel()

    private lateinit var adapter: IncludeNotesAdapter

    companion object {
        const val NEW_CHECKED_NOTES = "new_checked_notes"

        private const val INCLUDE_NOTES_SELECTION_ID = "include_notes_selection_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_folders_include_notes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        initAdapter()
        initListeners()
    }

    private fun observeData() {
        observe(viewModel.noteListLiveData) { fullNoteList ->
            when (fullNoteList.status) {
                Status.LOADING -> {
                    //displayProgress()
                }
                Status.SUCCESS -> {
                    setData(fullNoteList.data, viewModel.getNotesById(getCheckedNoteIds()))
                }
                Status.ERROR -> {
                    //displayError(it.message ?: "")
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = IncludeNotesAdapter(requireActivity())
        rv_notes.adapter = adapter

        val tracker = SelectionTracker.Builder<NoteEntity>(
            INCLUDE_NOTES_SELECTION_ID,
            rv_notes,
            IncludeNotesKeyProvider(adapter),
            IncludeNotesLookup(rv_notes),
            StorageStrategy.createParcelableStorage(NoteEntity::class.java)
        )
            .build()
        adapter.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<NoteEntity>() {

            override fun onItemStateChanged(key: NoteEntity, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                if (selected)
                    addChip(key as NoteEntity)
                else
                    removeChip(key as NoteEntity)
            }
        })
    }

    private fun initListeners() {
        sv_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        fab_done.setSafeOnClickListener {
            passDataToBackStack()
        }
    }

    private fun passDataToBackStack() {
        val noteIds = ArrayList<Int>()
        val chipCount = chg_added_notes.childCount
        for (index in 0 until chipCount) {
            val chip = chg_added_notes.getChildAt(index) as Chip
            noteIds.add(chip.tag as Int)
        }

        findNavController().apply {
            previousBackStackEntry?.savedStateHandle?.set(NEW_CHECKED_NOTES, noteIds)
            navigateUp()
        }
    }

    private fun setData(noteList: List<NoteEntity>?, checkedNoteIds: List<NoteEntity>? = null) {
        noteList?.let {
            adapter.setList(it)
            checkedNoteIds?.let {
                adapter.tracker.setItemsSelected(checkedNoteIds, true)
            }
        }
    }

    private fun addChip(note: NoteEntity) {
        val existingChip = chg_added_notes.findViewWithTag<Chip>(note.id)
        if (existingChip == null) {
            val newChip = Chip(requireContext())
            newChip.text = note.description
            newChip.tag = note.id
            newChip.isCloseIconVisible = true
            newChip.setOnCloseIconClickListener {
                adapter.tracker.deselect(note)
            }

            chg_added_notes.addView(newChip)
        }
    }

    private fun removeChip(note: NoteEntity) {
        val chip = chg_added_notes.findViewWithTag<Chip>(note.id)
        chip?.let {
            chg_added_notes.removeView(it)
        }
    }

    private fun getCheckedNoteIds(): List<Int> {
        val args: IncludeNotesFragmentArgs by navArgs()
        return args.includedNoteIds.toList()
    }
}