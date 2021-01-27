package com.markel.passwordreminder.ui.page_fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.markel.passwordreminder.database.entity.GroupEntity

class SectionsPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private var groupList: List<GroupEntity> = listOf()

    // +1 for first "All" page
    override fun getItemCount() = groupList.size + 1

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> MainPageFragment.newInstance()
        else -> AdditionalPageFragment.newInstance(groupList[position - 1].id)
    }

    fun setGroups(groups: List<GroupEntity>) {
        groupList = groups as ArrayList<GroupEntity>
        notifyDataSetChanged()
    }

    fun getPageName(position: Int) = groupList[position - 1].name
    fun getPageGroupId(position: Int) = groupList[position - 1].id
}