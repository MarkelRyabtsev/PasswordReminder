package com.markel.passwordreminder.ui.page_fragment

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.markel.passwordreminder.database.entity.GroupEntity

class SectionsPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private var groupList: List<GroupEntity> = listOf()

    override fun getItemCount() = groupList.size

    override fun createFragment(position: Int) = PageFragment.newInstance(groupList[position].id)

    fun setGroups(groups: List<GroupEntity>) {
        groupList = groups as ArrayList<GroupEntity>
        notifyDataSetChanged()
    }

    fun getPageName(position: Int) = groupList[position].name
    fun getPageGroupId(position: Int) = groupList[position].id
}