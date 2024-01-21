package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.entity.utills.AndroidUtils
import ru.netology.nmedia.entity.utills.RevealAnimationSetting
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSavePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.requests.PostCreateRequest
import ru.netology.nmedia.viewModel.PostViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class SavePostFragment : DialogFragment() {
    @Inject
    lateinit var errorCallback:ErrorCallback
    private var editedPost:Post? = null
    private var revealAnimationSetting: RevealAnimationSetting? = null
    private val postViewModel: PostViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(0)
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editedPost = it.getSerializable("post") as Post?
            revealAnimationSetting = it.getSerializable("revealAnimationSetting") as RevealAnimationSetting
        }
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val savePostFragmentBinding = FragmentSavePostBinding.inflate(layoutInflater)
        postViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                savePostFragmentBinding.apply {
                    progressBar.isVisible = loading
                }
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
                if(isSaved){
                    findNavController().popBackStack()
                }
            }
        }
        val contentEditText = savePostFragmentBinding.contentEditText.apply {
            text.clear()
        }
        val linkEditText = savePostFragmentBinding.contentEditText.apply {
            text.clear()
        }
        val button = savePostFragmentBinding.save
        val cancelButton = savePostFragmentBinding.cancelButton
        if (editedPost == null) {
            button.setIconResource(R.drawable.baseline_create_24)
            button.text = getString(R.string.create)
            savePostFragmentBinding.linearLayoutUpdate.visibility = View.GONE
            button.setOnClickListener {
                if (contentEditText.text.isBlank())
                    Snackbar.make(savePostFragmentBinding.root,getString(R.string.empty_content_message),Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){
                    }.show()
                else {
                    postViewModel.savePost(PostCreateRequest(content = contentEditText.text.toString(), link = linkEditText.text.toString().ifBlank { null }))
                }
            }
        } else {
            editedPost?.let {post->
                button.setIconResource(R.drawable.baseline_save_as_24)
                button.text = "EDIT"
                contentEditText.setText(post.content)
                linkEditText.setText(post.link)
                savePostFragmentBinding.linearLayoutUpdate.visibility = View.VISIBLE
                button.setOnClickListener {
                    if (contentEditText.text.isBlank()) {
                        Snackbar.make(
                            savePostFragmentBinding.root,
                            "Контент не может быть пустым",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(android.R.string.ok) {
                        }.show()
                    } else {
                        postViewModel.savePost(PostCreateRequest(post.copy(content = contentEditText.text.toString(),link=linkEditText.text.toString().ifBlank { null } )))
                    }
                }
                cancelButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        AndroidUtils.registerCircularRevealAnimation(savePostFragmentBinding.root,revealAnimationSetting!!
        )
        return savePostFragmentBinding.root
    }
}