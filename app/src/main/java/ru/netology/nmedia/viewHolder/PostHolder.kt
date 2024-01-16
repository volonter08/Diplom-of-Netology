package ru.netology.nmedia.viewHolder

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.utills.ConverterCountFromIntToString
import ru.netology.nmedia.enumeration.AttachmentType


class PostHolder(
    val context: Context,
    val binding: PostBinding,
    private val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        val avatarImageView = binding.avatar
        val authorTextView = binding.author
        val dateTextView = binding.date
        val menuButton = binding.menu
        val contentTextView = binding.content
        val linkTextView = binding.link
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
        val shareButton = binding.share
        val mentionedButton = binding.mentioned

        avatarImageView.also {
            if (post.authorAvatar != null) {
                val animPlaceholder =
                    context.getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceholder.start() // probably needed
                Glide.with(context).load(Uri.parse(post.authorAvatar)).placeholder(animPlaceholder)
                    .error(R.drawable.avatar_svgrepo_com).timeout(10_000).circleCrop().into(it)
            } else {
                it.setImageResource(R.drawable.null_avatar)
            }
            it.setOnClickListener {
                listener.onPostAuthorClick(post.authorId)
            }
        }
        authorTextView.text = post.author
        dateTextView.text = post.published.let{
            val dateFormatOut: DateFormat = SimpleDateFormat("HH:mm:ss   dd/MM/yyyy")
            dateFormatOut.format(it)
        }
        menuButton.apply {
            isVisible = post.ownedByMe
            setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.remove -> {
                                listener.onRemoveClick(post)
                                true
                            }

                            R.id.update -> {
                                listener.onUpdateCLick(post)
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
        contentTextView.text = post.content
        linkTextView.apply {
            text = post.link ?: ""
            isVisible = urls.isNotEmpty()
        }
        if (post.attachment != null) {
            when (post.attachment.type) {
                AttachmentType.IMAGE -> {
                    attachmentImageImageView.apply {
                        isVisible = true
                        Glide.with(this).load(post.attachment.url).into(this)
                    }
                }
                AttachmentType.VIDEO -> {
                    val player = ExoPlayer.Builder(context).build()
                    val mediaItem = MediaItem.fromUri(Uri.parse(post.attachment.url))
                    player.setMediaItem(mediaItem)
                    attachmentVideoPlayerView.apply {
                        isVisible = true
                        this.player = player
                    }
                    player.prepare()
                }

                AttachmentType.AUDIO -> {
                    val player = ExoPlayer.Builder(context).build()
                    val mediaItem = MediaItem.fromUri(Uri.parse(post.attachment.url))
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
            text = ConverterCountFromIntToString.convertCount(post.likeOwnerIds.size)
            setOnClickListener {
                if (!post.likedByMe)
                    listener.onLikeCLick(post)
                else
                    listener.onDislikeCLick(post)
            }
            addOnCheckedChangeListener { button, isChecked ->
                button.isChecked = post.likedByMe
            }
            isChecked = post.likedByMe
        }
        shareButton.setOnClickListener {
            listener.onShareCLick(post)
        }
        mentionedButton.text = ConverterCountFromIntToString.convertCount(post.mentionIds.size)
        //println("visible_image_view:${binding.attachmentImage.isVisible} attachment = ${post.attachment} content: ${post.content} ")
    }
}