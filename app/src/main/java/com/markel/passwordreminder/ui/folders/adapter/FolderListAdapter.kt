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
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.setSafeOnClickListener
import com.markel.passwordreminder.util.AdapterDiffUtil
import com.markel.passwordreminder.util.bindView

class FolderListAdapter(
    context: Context,
    private val moreActionsClickListener: (GroupEntity?) -> Unit,
    private val itemClickListener: (GroupEntity?) -> Unit
) : RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {

    var adapterList = ArrayList<GroupEntity>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.item_folders_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = adapterList[position]

        holder.folderName.text = folder.name
        holder.buttonMore.setSafeOnClickListener {
            moreActionsClickListener.invoke(folder)
        }
        holder.itemView.setSafeOnClickListener {
            itemClickListener.invoke(folder)
        }
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

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                adapterList[i] = adapterList.set(i + 1, adapterList[i]);
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                adapterList[i] = adapterList.set(i - 1, adapterList[i]);
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val buttonDrag: ImageView by bindView(R.id.iv_drag_item)
        val folderName: TextView by bindView(R.id.tv_folder_name)
        val buttonMore: ImageView by bindView(R.id.iv_more_action)
    }
}