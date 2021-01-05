package com.markel.passwordreminder.ui.page_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.ext.hideGroupViews
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.show
import com.markel.passwordreminder.ui.page_fragment.note.NoteAdapter
import kotlinx.android.synthetic.main.fragment_page.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PageFragment : Fragment(R.layout.fragment_page),
    SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: PageViewModel by sharedViewModel()

    private lateinit var noteAdapter: NoteAdapter

    companion object {

        private const val GROUP_ID = "group_id"

        @JvmStatic
        fun newInstance(groupId: Int): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(GROUP_ID, groupId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initAdapter()
        observeData()

        viewModel.getNotes(getGroupId())
    }

    private fun initListeners() {
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun initAdapter() {
        noteAdapter = NoteAdapter(this.requireContext())
        rvNotes.adapter = noteAdapter
    }

    private fun observeData() {
        observe(viewModel.isDataLoaded) {
            when (it.status) {
                Status.LOADING -> displayProgress()
                Status.SUCCESS -> {
                    it.data?.let { isLoaded ->
                        if (isLoaded) getData()
                        else displayTextByEmptyList()
                    }
                    displayNormal()
                }
                Status.ERROR -> {
                    displayError(it.message ?: "")
                }
            }
        }
    }

    override fun onRefresh() {
        viewModel.updateNotes()
    }

    fun displayProgress() {
        progressBar.show()
        hideGroupViews(rvNotes, tvError)
    }

    fun displayNormal() {
        rvNotes.show()
        hideGroupViews(progressBar, tvError)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun displayTextByEmptyList() {
        tvError.show()
    }

    private fun displayError(text: String) {
        tvError.show()
        hideGroupViews(progressBar, rvNotes)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun getData() {
        noteAdapter.setList(viewModel.getNotes(getGroupId()))
    }

    private fun getGroupId() = arguments?.getInt(GROUP_ID) ?: 1
}