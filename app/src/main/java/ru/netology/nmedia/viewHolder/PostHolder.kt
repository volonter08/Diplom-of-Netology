package ru.netology.nmedia.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedImageDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.netologyandroidhomework1.AndroidUtils
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.apiModule.ApiModule.BASE_URL
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.utills.ConverterCountFromIntToString
import java.net.URL


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
                Glide.with(context).load(Uri.parse(post.authorAvatar)).placeholder(animPlaceholder)
                    .error(R.drawable.null_avatar).timeout(10_000).circleCrop().into(it)
            }
            else {
                it.setImageResource(R.drawable.null_avatar)
            }
            it.setOnClickListener {
                listener.onPostAuthorClick(post.authorId)
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
            like.text = ConverterCountFromIntToString.convertCount(post.likeOwnerIds.size)
            like.isChecked = post.likedByMe
        }
    }
}