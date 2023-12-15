package ru.netology.nmedia.viewHolder

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.apiModule.ApiModule
import ru.netology.nmedia.databinding.EventBinding

import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.utills.ConverterCountFromIntToString
import kotlin.math.min

class EventHolder(val context: Context, val eventBinding:EventBinding,val listener: OnButtonTouchListener,val parent:ViewGroup):RecyclerView.ViewHolder(eventBinding.root) {
    fun bind(event: Event){
        /*parent.post {
            eventBinding.apply {
                val minSizeValue = min(parent.width,parent.height)
                avatar.layoutParams.height = minSizeValue/8
                avatar.requestLayout()
                like.apply {
                    iconSize = minSizeValue/12
                    setPadding(iconSize,iconSize,0,iconSize)
                }
                share.apply {
                    iconSize = minSizeValue/12
                    setPadding(iconSize,iconSize,0,iconSize)
                }
                mentioned.apply {
                    mentioned.iconSize = minSizeValue/12
                    setPadding(iconSize,0,0,iconSize)
                }
            }
        }

         */
        val likeButton = eventBinding.like
        val shareButton = eventBinding.share
        eventBinding.avatar.also {
            if (event.authorAvatar != null) {
                val animPlaceholder =
                    context.getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceholder.start() // probably needed
                Glide.with(context).load(Uri.parse(event.authorAvatar)).placeholder(animPlaceholder).error(
                    R.drawable.null_avatar).timeout(10_000).circleCrop().into(it)
            }
        }
        eventBinding.attachment.also {
            if (event.attachment != null) {
                it.visibility = View.VISIBLE
                Glide.with(context).load(Uri.parse("${ApiModule.BASE_URL}images/${event.attachment.url}"))
                    .timeout(10_000).into(it)
            } else {
                it.visibility = View.GONE
            }
        }
        eventBinding.menu.isVisible = event.ownedByMe
        eventBinding.menu.setOnClickListener { menu ->
            PopupMenu(menu.context, menu).apply {
                inflate(R.menu.options)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClick(event)
                            true
                        }

                        R.id.update -> {
                            listener.onUpdateCLick(event)
                            true
                        }

                        else -> false
                    }
                }
            }
        }
        eventBinding.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClick(event)
                            true
                        }

                        R.id.update -> {
                            listener.onUpdateCLick(event)
                            true
                        }

                        else -> false
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    setForceShowIcon(true)

            }.show()
        }
        likeButton.setOnClickListener {
            if (!event.likedByMe)
                listener.onLikeCLick(event)
            else
                listener.onDislikeCLick(event)
        }
        shareButton.setOnClickListener {
            listener.onShareCLick(event)
        }
        likeButton.addOnCheckedChangeListener { button, isChecked ->
            button.isChecked = event.likedByMe
        }
        eventBinding.apply {
            author.text = event.author
            date.text = event.published.toString()
            content.text = event.content
            like.text = ConverterCountFromIntToString.convertCount(event.likeOwnerIds.size)
            like.isChecked = event.likedByMe
        }
    }
}