package ru.netology.nmedia.viewHolder

import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.os.Build
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
    private val binding: PostBinding,
    val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    init{

    }
    fun bind(post: Post) {
        val likeButton = binding.like
        val shareButton = binding.share
        binding.avatar.also {
            if (post.authorAvatar != null) {
                val animPlaceholder =
                    context.getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceholder.start() // probably needed
                Glide.with(context).load(Uri.parse(post.authorAvatar)).placeholder(animPlaceholder)
                    .error(R.drawable.null_avatar).timeout(10_000).circleCrop().into(it)
            } else {
                it.setImageResource(R.drawable.null_avatar)
            }
            it.setOnClickListener {
                listener.onPostAuthorClick(post.authorId)
            }
        }

        binding.menu.isVisible = post.ownedByMe
        binding.menu.setOnClickListener {
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
        post.link?.let {
            binding.link.apply {
                text = it
                isVisible = urls.isNotEmpty()
            }
        }
        likeButton.setOnClickListener {
            if (!post.likedByMe)
                listener.onLikeCLick(post)
            else
                listener.onDislikeCLick(post)
        }
        shareButton.setOnClickListener {
            listener.onShareCLick(post)
        }
        likeButton.addOnCheckedChangeListener { button, isChecked ->
            button.isChecked = post.likedByMe
        }
        binding.apply {
            author.text = post.author
            date.text = post.published.toString()
            content.text = post.content
            post.link?.let {
                link.apply {
                    text = it
                    isVisible = urls.isNotEmpty()
                }
            }
            like.text = ConverterCountFromIntToString.convertCount(post.likeOwnerIds.size)
            like.isChecked = post.likedByMe
            mentioned.text = ConverterCountFromIntToString.convertCount(post.mentionIds.size)
        }
        post.attachment?.let {
            val player = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(Uri.parse(it.url))
            player.setMediaItem(mediaItem)
            player.prepare()
            when (post.attachment.type) {
                AttachmentType.IMAGE -> {
                }

                AttachmentType.VIDEO -> {
                    binding.attachmentVideo.apply {
                        isVisible = true
                        this.player = player
                    }
                }
                AttachmentType.AUDIO -> {
                    binding.attachmentAudio.apply {
                        isVisible = true
                        this.player = player
                    }
                }
            }

        }
    }
}