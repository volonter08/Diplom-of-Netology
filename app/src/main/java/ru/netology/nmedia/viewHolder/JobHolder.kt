package ru.netology.nmedia.viewHolder

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.JobBinding
import ru.netology.nmedia.dto.Job
import java.text.SimpleDateFormat

class JobHolder(
    val binding: JobBinding,
    val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(job: Job) {
        binding.apply {
            nameJob.text = job.name
            positionJob.text = job.position
            startJob.text = SimpleDateFormat("dd MMM yyyy").format(job.start)
            finishJob.isVisible = job.finish !=null
            job.finish?.let {
                finishJob.text = SimpleDateFormat("dd MMM yyyy").format(it)
            }
            linkJob.isVisible = job.link!=null
            job.link?.let{
                linkJob.text = it
                linkJob.isVisible = linkJob.urls.isNotEmpty()
            }
            menu.apply {
                isVisible = job.ownedMe
                setOnTouchListener {view,motionEvent->
                    PopupMenu(view.context, view).apply {
                        inflate(R.menu.options)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.remove -> {
                                    listener.onRemoveClick(job)
                                    true
                                }

                                R.id.update -> {
                                    listener.onUpdateCLick(job,
                                        Point(motionEvent.rawX.toInt(),motionEvent.rawY.toInt())
                                    )
                                    true
                                }

                                else -> false
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                            setForceShowIcon(true)

                    }.show()
                    return@setOnTouchListener true
                }
            }
        }
    }
}