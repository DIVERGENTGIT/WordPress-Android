package org.wordpress.android.ui.pages

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView.Adapter
import android.view.ViewGroup
import org.wordpress.android.ui.pages.PageItem.Page
import org.wordpress.android.ui.pages.PageItem.ParentPage
import org.wordpress.android.ui.pages.PageItemViewHolder.EmptyViewHolder
import org.wordpress.android.ui.pages.PageItemViewHolder.PageDividerViewHolder
import org.wordpress.android.ui.pages.PageItemViewHolder.PageParentViewHolder
import org.wordpress.android.ui.pages.PageItemViewHolder.PageViewHolder

class PagesAdapter(
    private val onMenuAction: (PageItem.Action, Page) -> Boolean = { _, _ -> false },
    private val onItemTapped: (Page) -> Unit = { _ -> Unit },
    private val onParentSelected: (ParentPage) -> Unit = { _ -> Unit }
) : Adapter<PageItemViewHolder>() {
    private val items = mutableListOf<PageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageItemViewHolder {
        return when (viewType) {
            PageItem.Type.PAGE.viewType -> PageViewHolder(parent, onMenuAction, onItemTapped)
            PageItem.Type.DIVIDER.viewType -> PageDividerViewHolder(parent)
            PageItem.Type.EMPTY.viewType -> EmptyViewHolder(parent)
            PageItem.Type.PARENT.viewType -> PageParentViewHolder(parent, onParentSelected, this)
            else -> throw IllegalArgumentException("Unexpected view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].type.viewType
    }

    override fun onBindViewHolder(holder: PageItemViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    fun update(result: List<PageItem>) {
        val diffResult = DiffUtil.calculateDiff(PageItemDiffUtil(items.toList(), result))
        items.clear()
        items.addAll(result)
        diffResult.dispatchUpdatesTo(this)
    }

    private class PageItemDiffUtil(val items: List<PageItem>, val result: List<PageItem>) : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = items[oldItemPosition]
            val newItem = result[newItemPosition]
            return oldItem.type == newItem.type && when (oldItem) {
                is Page -> oldItem.id == (newItem as Page).id
                is ParentPage -> oldItem.id == (newItem as ParentPage).id
                else -> oldItem == newItem
            }
        }

        override fun getOldListSize(): Int = items.size

        override fun getNewListSize(): Int = result.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return items[oldItemPosition] == result[newItemPosition]
        }
    }
}
