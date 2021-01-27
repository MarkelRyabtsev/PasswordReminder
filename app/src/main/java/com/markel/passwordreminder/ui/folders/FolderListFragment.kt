package com.markel.passwordreminder.ui.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ui.folders.adapter.FolderListAdapter
import com.markel.passwordreminder.ui.main.GroupViewModel
import kotlinx.android.synthetic.main.folders_fragment_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListFragment : Fragment() {

    private val viewModel: GroupViewModel by viewModel()

    private lateinit var folderAdapter: FolderListAdapter

    companion object {
        fun newInstance() = FolderListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.folders_fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        initAdapter()
    }

    private fun observeData() {
        observe(viewModel.groupListLiveData) {
            when (it.status) {
                Status.LOADING -> {
                    //displayProgress()
                }
                Status.SUCCESS -> setData(it.data)
                Status.ERROR -> {
                    //displayError(it.message ?: "")
                }
            }
        }
    }

    private fun initAdapter() {
        folderAdapter = FolderListAdapter(requireActivity())
        rv_folder_list.adapter = folderAdapter
    }

    private fun setData(groupList: List<GroupEntity>?) {
        groupList?.let {
            folderAdapter.setList(it)
        }
    }
}