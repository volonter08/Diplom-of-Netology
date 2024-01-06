package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSavePostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.PostViewModelFactory
import javax.inject.Inject

@AndroidEntryPoint
class SavePostFragment : Fragment() {
    @Inject
    lateinit var errorCallback:ErrorCallback
    private var editedPost:Post? = null
    private val postViewModel: PostViewModel by viewModels<PostViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(-1)
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            editedPost = it.getSerializable("post") as Post?
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val savePostFragmentBinding = FragmentSavePostBinding.inflate(layoutInflater)
        postViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                savePostFragmentBinding.apply {
                    progressBar.isVisible = loading
                    mainLayoutSavePost.isVisible = loading
                }
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
                if(isPostCreated){
                    findNavController().popBackStack()
                }
            }
        }
        val editText = savePostFragmentBinding.contentEditText.apply {
            text.clear()
        }
        val button = savePostFragmentBinding.save
        val cancelButton = savePostFragmentBinding.cancelButton
        if (editedPost == null) {
            button.setIconResource(R.drawable.baseline_create_24)
            button.setText("CREATE")
            savePostFragmentBinding.linearLayoutUpdate.visibility = View.GONE
            button.setOnClickListener {
                if (editText.text.isBlank())
                    Snackbar.make(savePostFragmentBinding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){
                    }.show()
                else {
                    postViewModel.createPost(editText.text.toString())
                }
            }
        } else {
            editedPost?.let {post->
                button.setIconResource(R.drawable.baseline_save_as_24)
                button.text = "EDIT"
                savePostFragmentBinding.updateContentText.text =post.content
                editText.setText(post.content)
                savePostFragmentBinding.linearLayoutUpdate.visibility = View.VISIBLE
                button.setOnClickListener {
                    if (editText.text.isBlank()) {
                        Snackbar.make(
                            savePostFragmentBinding.root,
                            "Контент не может быть пустым",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(android.R.string.ok) {
                        }.show()
                    } else {
                        postViewModel.update(post.copy(content = editText.text.toString()))
                    }
                }
                cancelButton.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
        return savePostFragmentBinding.root
    }
}