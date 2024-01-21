package ru.netology.nmedia.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.MotionEvent
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.JobBinding
import ru.netology.nmedia.dto.Job
import java.text.SimpleDateFormat

class JobHolder(
    val context: Context,
    private val binding: JobBinding,
    private val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(job: Job) {
        binding.apply {
            nameJob.text =
                context.getString(R.string.name_format, job.name)
            positionJob.text = context.getString(R.string.position_format,job.position)
            workDateJob.text = context.getString(
                R.string.work_date,
                SimpleDateFormat("dd MMM yyyy").format(job.start),
                job.finish?.let { SimpleDateFormat("dd MMM yyyy").format(it) } ?: context.getString(
                    R.string.p_d
                )
            )
            job.link?.let {
                linkJob.text = it
            }
            linkJob.isVisible = linkJob.urls.isNotEmpty()
            menuJob.apply {
                isVisible = job.ownedMe
                setOnTouchListener{view,motionEvent->
                    val point =  Point(motionEvent.rawX.toInt(),motionEvent.rawY.toInt())
                    if (motionEvent.action== MotionEvent.ACTION_DOWN) {
                        PopupMenu(view.context, view).apply {
                            inflate(R.menu.options)
                            setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.remove -> {
                                        listener.onRemoveClick(job)
                                        true
                                    }

                                    R.id.update -> {
                                        listener.onUpdateCLick(job,point)
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
                    return@setOnTouchListener false
                }
            }
        }
    }
}