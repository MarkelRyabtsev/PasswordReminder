package com.markel.passwordreminder.ui.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.markel.passwordreminder.R
import com.markel.passwordreminder.base.vo.Status
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.ext.observe
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.ui.folders.adapter.DragManageAdapter
import com.markel.passwordreminder.ui.folders.adapter.FolderListAdapter
import com.markel.passwordreminder.ui.main.GroupViewModel
import kotlinx.android.synthetic.main.folders_activity.*
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
        initListeners()
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

        val callback = DragManageAdapter(folderAdapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(rv_folder_list)
    }

    private fun initListeners() {
        fl_add_folder.setSafeOnClickListener {
            findNavController().navigate(R.id.action_folderListFragment_to_newFolderFragment)
        }
    }

    private fun setData(groupList: List<GroupEntity>?) {
        groupList?.let {
            folderAdapter.setList(it)
        }
    }
}