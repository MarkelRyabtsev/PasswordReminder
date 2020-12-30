package com.markel.passwordreminder.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Класс для работы со списками
 * Может работать с несколькими видами элементов
 * В качестве viewType выступает лэйаут элемента [DisplayItem.layout]
 * поэтому, в пределах одного списка все лэйауты должны быть уникальными
 * для одинаковых лэйаутов можно использовать <include>
 *
 * Для работы нужно обозначить DTO с их лэйаутами и переопределить [createViewHolder]
 * Все элементы должны имплементить интерфейс [DisplayItem]
 *
 * см. [AchievementAdapter] для примера
 */
abstract class DisplayAdapter(
    items: List<DisplayItem> = emptyList()
) : RecyclerView.Adapter<DisplayViewHolder<DisplayItem>>() {

    open var items = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int) = items[position].layout

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DisplayViewHolder<DisplayItem> = createViewHolder(
        LayoutInflater.from(parent.context).inflate(
            viewType,
            parent,
            false
        ), viewType
    ) as DisplayViewHolder<DisplayItem>

    /**
     * Erased type for brevity, item should always implement [DisplayItem]
     */
    abstract fun createViewHolder(view: View, @LayoutRes viewType: Int): DisplayViewHolder<*>

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DisplayViewHolder<DisplayItem>, position: Int) =
        holder.bind(items[position])
}

/**
 * Общий интерфейс для элементов отображения в списке адаптера
 */
abstract class DisplayItem(val layout: Int)

/**
 * Общий интерфейс для вьюхолдеров
 * Связывает элементы [DisplayItem] и их вьюшки [DisplayItem.layout]
 * Обычно лучше делать inner-классом элемента для понятности, но не обязательно
 */
abstract class DisplayViewHolder<E>(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    abstract fun bind(item: E)
}