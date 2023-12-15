package ru.netology.nmedia.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.databinding.EventBinding
import ru.netology.nmedia.diffutill.EventDiffCallback
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.viewHolder.EventHolder

class EventAdapter(
    val context: Context,
    private val listener: OnButtonTouchListener
) : PagingDataAdapter<Event, EventHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = EventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(parent.context, binding, listener,parent)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}