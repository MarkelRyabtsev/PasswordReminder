package com.markel.passwordreminder.ui.folders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.R
import com.markel.passwordreminder.database.entity.GroupEntity
import com.markel.passwordreminder.util.AdapterDiffUtil
import com.markel.passwordreminder.util.bindView

class FolderListAdapter(context: Context) : RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

    var adapterList = ArrayList<GroupEntity>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.item_folders_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = adapterList[position]

        holder.folderName.text = folder.name
    }

    override fun getItemCount() = adapterList.size

    fun setList(list: List<GroupEntity>) {
        DiffUtil.calculateDiff(
            AdapterDiffUtil(adapterList, list)
        ).dispatchUpdatesTo(this)
        adapterList.clear()
        adapterList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val buttonDrag: ImageView by bindView(R.id.iv_drag_item)
        val folderName: TextView by bindView(R.id.tv_folder_name)
        val buttonMore: ImageView by bindView(R.id.iv_more_action)
    }
}