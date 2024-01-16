package ru.netology.nmedia.diffutill

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.Job

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean  =
        oldItem.id==newItem.id

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean =
        oldItem==newItem
}