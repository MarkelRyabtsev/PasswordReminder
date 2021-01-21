package com.markel.passwordreminder.ui.page_fragment

import android.os.Bundle
import android.view.View
import com.markel.passwordreminder.base.fragment.BasePageFragment

class AllNotesPageFragment : BasePageFragment() {

    companion object {

        @JvmStatic
        fun newInstance() = AllNotesPageFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateNotes()
    }
}