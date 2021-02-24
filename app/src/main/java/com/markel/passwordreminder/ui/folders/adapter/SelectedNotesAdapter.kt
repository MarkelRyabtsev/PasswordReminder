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
import com.markel.passwordreminder.ext.show
import com.markel.passwordreminder.util.AdapterDiffUtil
import com.markel.passwordreminder.util.bindView

class SelectedNotesAdapter(
    context: Context,
    private val listChangedListener: () -> Unit
) :
    RecyclerView.Adapter<SelectedNotesAdapter.ViewHolder>() {

    var adapterList = ArrayList<NoteEntity>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.item_folders_included_note, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = adapterList[position]

        holder.noteDescription.text = note.description
        holder.removeButton.show()
        holder.removeButton.setSafeOnClickListener { removeFromList(position) }
    }

    override fun getItemCount() = adapterList.size

    fun setList(list: List<NoteEntity>) {
        updateList(adapterList, list)
    }

    fun getList() = adapterList.toList()

    private fun removeFromList(position: Int) {
        val updatedList = ArrayList(adapterList)
        updatedList.removeAt(position)
        updateList(adapterList, updatedList)

        listChangedListener.invoke()
    }

    private fun updateList(oldList: List<NoteEntity>, newList: List<NoteEntity>) {
        DiffUtil.calculateDiff(
            AdapterDiffUtil(oldList, newList)
        ).dispatchUpdatesTo(this)
        adapterList.clear()
        adapterList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteDescription: TextView by bindView(R.id.tv_note_description)
        val removeButton: ImageView by bindView(R.id.iv_remove)
    }
}