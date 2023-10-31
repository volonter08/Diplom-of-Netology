package ru.netology.nmedia.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.apiModule.ApiModule.BASE_URL
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.utills.ConverterCountFromIntToString


class PostHolder(
    val context: Context,
    private val binding: PostBinding,
    val listener: OnButtonTouchListener
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(post: Post) {
        val likeButton = binding.like
        val shareButton = binding.share
        binding.avatar.also {
            if (post.authorAvatar != null) {
                val animPlaceholder =
                    context.getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceholder.start() // probably needed
                Glide.with(context).load(Uri.parse("${BASE_URL}avatars/${post.authorAvatar}"))
                    .placeholder(animPlaceholder).timeout(10_000).circleCrop().into(it)
            }
        }
        binding.attachment.also {
            if (post.attachment != null) {
                it.visibility = View.VISIBLE
                Glide.with(context).load(Uri.parse("${BASE_URL}images/${post.attachment.url}"))
                    .timeout(10_000).into(it)
            } else {
                it.visibility = View.GONE
            }
        }
        binding.menu.isVisible = post.ownedByMe
        binding.menu.setOnClickListener { menu ->
            PopupMenu(menu.context, menu).apply {
                inflate(R.menu.options)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClick(post.id)
                            true
                        }

                        R.id.update -> {
                            listener.onUpdateCLick(post)
                            true
                        }

                        else -> false
                    }
                }
            }
        }
        binding.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClick(post.id)
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
        likeButton.setOnClickListener {
            if (!post.likedByMe)
                listener.onLikeCLick(post.id)
            else
                listener.onDislikeCLick(post.id)
        }
        shareButton.setOnClickListener {
            listener.onShareCLick(post)
        }

        binding.apply {
            author.text = post.author
            date.text = post.published.toString()
            content.text = post.content
            like.text = ConverterCountFromIntToString.convertCount(post.likeOwnerIds.size)
            like.isChecked = post.likedByMe
        }
    }
}