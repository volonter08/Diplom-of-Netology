package ru.netology.nmedia.viewHolder

import android.os.Build
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.JobBinding
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.dto.Post

class JobHolder(
    val binding: JobBinding,
    val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            nameJob.text = job.name
            positionJob.text = job.position
            startJob.text = job.start
            finishJob.isVisible = job.finish !=null
            job.finish?.let {
                finishJob.text = it
            }
            linkJob.isVisible = job.link!=null
            job.link?.let{
                linkJob.text = it
                linkJob.isVisible = linkJob.urls.isNotEmpty()
            }
            menu.apply {
                isVisible = job.ownedMe
                setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        inflate(R.menu.options)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.remove -> {
                                    listener.onRemoveClick(job)
                                    true
                                }

                                R.id.update -> {
                                    listener.onUpdateCLick(job)
                                    true
                                }

                                else -> false
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            setForceShowIcon(true)

                    }.show()
                }
            }
        }
    }
}