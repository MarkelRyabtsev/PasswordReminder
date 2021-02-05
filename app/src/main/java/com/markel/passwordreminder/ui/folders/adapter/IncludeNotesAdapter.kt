package com.markel.passwordreminder.ui.folders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.markel.passwordreminder.R
import com.markel.passwordreminder.database.entity.NoteEntity
import com.markel.passwordreminder.ext.hide
import com.markel.passwordreminder.ext.show
import com.markel.passwordreminder.ui.page_fragment.note.IncludeNoteItemDetails
import com.markel.passwordreminder.ui.page_fragment.note.ViewHolderWithDetails
import com.markel.passwordreminder.util.AdapterDiffUtil
import com.markel.passwordreminder.util.bindView
import java.util.*
import kotlin.collections.ArrayList

class IncludeNotesAdapter(context: Context) : RecyclerView.Adapter<IncludeNotesAdapter.ViewHolder>(), Filterable {

    var adapterList = ArrayList<NoteEntity>()
    private var fullList = ArrayList<NoteEntity>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var tracker: SelectionTracker<NoteEntity>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(inflater.inflate(R.layout.item_folders_included_note, parent, false), adapterList)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = adapterList[position]

        holder.noteDescription.text = note.description
        holder.setActivatedState(tracker.isSelected(adapterList[position]))
    }

    override fun getItemCount() = adapterList.size

    fun setList(list: List<NoteEntity>, updateFullList: Boolean = true) {
        DiffUtil.calculateDiff(
            AdapterDiffUtil(adapterList, list)
        ).dispatchUpdatesTo(this)
        adapterList.clear()
        adapterList.addAll(list)

        if (updateFullList) {
            fullList.clear()
            fullList.addAll(list)
        }

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                val filteredList = if (charSearch.isEmpty()) {
                    fullList
                } else {
                    val resultList = ArrayList<NoteEntity>()
                    for (note in fullList) {
                        note.description?.let {
                            if (it.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                                resultList.add(note)
                            }
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                setList(results?.values as ArrayList<NoteEntity>, false)
            }
        }
    }

    class ViewHolder(itemView: View, private val items: List<NoteEntity>) : RecyclerView.ViewHolder(itemView),
        ViewHolderWithDetails<NoteEntity> {
        override fun getItemDetail() =
            IncludeNoteItemDetails(adapterPosition, items.getOrNull(adapterPosition))

        fun setActivatedState(isActivated: Boolean) {
            itemView.isActivated = isActivated
            when (isActivated) {
                true -> {
                    selectedIcon.show()
                }
                false -> {
                    selectedIcon.hide()
                }
            }
        }

        private val selectedIcon: ImageView by bindView(R.id.iv_selected_icon)
        val noteDescription: TextView by bindView(R.id.tv_note_description)
    }
}