package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.databinding.JobBinding
import ru.netology.nmedia.diffutill.JobDiffCallback
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.viewHolder.JobHolder

class JobAdapter(val listener:OnButtonTouchListener): ListAdapter<Job, JobHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        val jobBinding = JobBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return JobHolder(parent.context,jobBinding, listener)
    }

    override fun onBindViewHolder(holder: JobHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

}