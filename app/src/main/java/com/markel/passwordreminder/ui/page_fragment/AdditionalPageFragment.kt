package com.markel.passwordreminder.ui.page_fragment

import android.os.Bundle
import com.markel.passwordreminder.base.fragment.BasePageFragment

class AdditionalPageFragment : BasePageFragment() {

    companion object {

        private const val GROUP_ID = "group_id"

        @JvmStatic
        fun newInstance(groupId: Int): AdditionalPageFragment {
            return AdditionalPageFragment().apply {
                arguments = Bundle().apply {
                    putInt(GROUP_ID, groupId)
                }
            }
        }
    }

    override fun setData() {
        viewModel.getNotesByGroupId(getGroupId()).also {
            if (it.isEmpty())
                displayTextByEmptyList()
            else {
                displayNormal()
                noteAdapter.setList(it)
            }
        }
    }

    private fun getGroupId() = arguments?.getInt(GROUP_ID) ?: 1
}