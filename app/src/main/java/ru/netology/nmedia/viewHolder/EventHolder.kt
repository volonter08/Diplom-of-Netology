package ru.netology.nmedia.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.AnimatedImageDrawable
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.EventBinding

import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.utills.ConverterCountFromIntToString
import ru.netology.nmedia.enumeration.AttachmentType

class EventHolder(val context: Context, val binding:EventBinding,val listener: OnButtonTouchListener,val parent:ViewGroup):RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(event: Event){
        /*parent.event {
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
        likeButton.addOnCheckedChangeListener { button, isChecked ->
            button.isChecked = event.likedByMe
        }
        eventBinding.apply {
            author.text = event.author
            date.text = event.published.let{
                val dateFormatOut: DateFormat = SimpleDateFormat("HH:mm:ss   dd/MM/yyyy")
                dateFormatOut.format(it)
            }
            datetime.text = event.datetime.let{
                val dateFormatOut: DateFormat = SimpleDateFormat("HH:mm   dd MMM yyyy")
                dateFormatOut.format(it)
            }
            content.text = event.content
            event.link?.let {
                link.apply {
                    text = it
                    isVisible = urls.isNotEmpty()
                }
            }
            like.text = ConverterCountFromIntToString.convertCount(event.likeOwnerIds.size)
            like.isChecked = event.likedByMe
            countMembers.text = ConverterCountFromIntToString.convertCount(event.likeOwnerIds.size) + "\nmembers"
            speakers.text= ConverterCountFromIntToString.convertCount(event.speakerIds.size)
        }

         */
        val avatarImageView = binding.avatar
        val authorTextView = binding.author
        val dateTextView = binding.date
        val dateTimeTextView = binding.datetime
        val menuButton = binding.menu
        val contentTextView = binding.content
        val linkTextView = binding.link
        val countMembersTextView = binding.countMembers
        val attachmentImageImageView = binding.attachmentImage.apply {
            Glide.with(this).clear(this)
            isVisible = false
        }
        val attachmentVideoPlayerView = binding.attachmentVideo.apply {
            isVisible = false
        }
        val attachmentAudioPlayerView = binding.attachmentAudio.apply {
            isVisible = false
        }
        val likeButton = binding.like
        val participateButton = binding.participate
        val speakersButton = binding.speakers

        avatarImageView.also {
            if (event.authorAvatar != null) {
                val animPlaceholder =
                    context.getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceholder.start() // probably needed
                Glide.with(context).load(Uri.parse(event.authorAvatar)).placeholder(animPlaceholder)
                    .error(R.drawable.avatar_svgrepo_com).timeout(10_000).circleCrop().into(it)
            } else {
                it.setImageResource(R.drawable.null_avatar)
            }
            it.setOnClickListener {
                listener.onPostAuthorClick(event.authorId)
            }
        }
        authorTextView.text = event.author
        dateTextView.text = event.published.let{
            val dateFormatOut: DateFormat = SimpleDateFormat("HH:mm:ss   dd/MM/yyyy")
            dateFormatOut.format(it)
        }
        dateTimeTextView.text = event.datetime.let{
            val dateFormatOut: DateFormat = SimpleDateFormat("HH:mm:ss   dd/MM/yyyy")
            dateFormatOut.format(it)
        }
        menuButton.apply {
            isVisible = event.ownedByMe
            setOnTouchListener {view,motionEvent->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.options)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.remove -> {
                                listener.onRemoveClick(event)
                                true
                            }

                            R.id.update -> {
                                listener.onUpdateCLick(event,
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
        contentTextView.text = event.content
        linkTextView.apply {
            text = event.link ?: ""
            isVisible = urls.isNotEmpty()
        }
        if (event.attachment != null) {
            when (event.attachment.type) {
                AttachmentType.IMAGE -> {
                    attachmentImageImageView.apply {
                        isVisible = true
                        Glide.with(this).load(event.attachment.url).into(this)
                    }
                }
                AttachmentType.VIDEO -> {
                    val player = ExoPlayer.Builder(context).build()
                    val mediaItem = MediaItem.fromUri(Uri.parse(event.attachment.url))
                    player.setMediaItem(mediaItem)
                    attachmentVideoPlayerView.apply {
                        isVisible = true
                        this.player = player
                    }
                    player.prepare()
                }

                AttachmentType.AUDIO -> {
                    val player = ExoPlayer.Builder(context).build()
                    val mediaItem = MediaItem.fromUri(Uri.parse(event.attachment.url))
                    player.setMediaItem(mediaItem)
                    attachmentAudioPlayerView.apply {
                        isVisible = true
                        this.player = player
                    }
                    player.prepare()
                }
            }
        }
        likeButton.apply {
            text = ConverterCountFromIntToString.convertCount(event.likeOwnerIds.size)
            setOnClickListener {
                if (!event.likedByMe)
                    listener.onLikeCLick(event)
                else
                    listener.onDislikeCLick(event)
            }
            addOnCheckedChangeListener { button, isChecked ->
                button.isChecked = event.likedByMe
            }
            isChecked = event.likedByMe
        }
        participateButton.apply {
            setOnClickListener {
                if (!event.participatedByMe)
                    listener.onParticipate(event.id)
                else
                    listener.onUnparticipate(event.id)
            }
            addOnCheckedChangeListener { button, isChecked ->
                button.isChecked = event.participatedByMe
            }
            isChecked = event.participatedByMe
        }
        countMembersTextView.text = ConverterCountFromIntToString.convertCount(event.participantsIds.size) + "\nmembers"
        speakersButton.text = ConverterCountFromIntToString.convertCount(event.speakerIds.size)
        //println("visible_image_view:${binding.attachmentImage.isVisible} attachment = ${event.attachment} content: ${event.content} ")
    }
}