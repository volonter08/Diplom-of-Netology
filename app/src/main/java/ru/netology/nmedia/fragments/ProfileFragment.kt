package ru.netology.nmedia.fragments

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentProfileBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.AuthViewModel
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.PostViewModelFactory
import ru.netology.nmedia.viewModel.ProfileViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val LOGIN = "login"
private const val NAME = "name"
private const val AVATAR = "avatar"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback
    @Inject
    lateinit var dataProfile:LiveData<Profile>
    // TODO: Rename and change types of parameters
    private var login: String? = null
    private var name: String? = null
    private var avatar: String? = null
    private val profileViewModel:ProfileViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels<PostViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(0)
            }
        }
    )
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            login = it.getString(LOGIN)
            name = it.getString(NAME)
            avatar = it.getString(AVATAR)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val onButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(likedNote: Note) {
                likedNote.run {
                    when (this) {
                        is Post -> postViewModel.like(this)
                        is Event -> {}
                    }
                }
            }

            override fun onDislikeCLick(dislikedNote: Note) {
                dislikedNote.run {
                    when (this) {
                        is Post -> postViewModel.dislike(this)
                        is Event -> {}
                    }
                }
            }

            override fun onShareCLick(note: Note) {

            }

            override fun onRemoveClick(removedNote: Note) {
                removedNote.run {
                    when (this) {
                        is Post -> postViewModel.remove(removedNote as Post)
                        is Event -> {}
                    }
                }
            }

            override fun onUpdateCLick(note: Note) {

            }

            override fun onCreateClick() {
            }
        }
        val postAdapter = PostAdapter(context = requireContext(), onButtonTouchListener)
        dataProfile.observe(viewLifecycleOwner){
            if(it.id == 0 && it.token == null){
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            }
            else if ( it.login==null && it.name==null && it.avatar==null){
                profileViewModel.initUserData(it.id.toString())
            }
            else{
                postAdapter.refresh()
            }
        }
        val profileFragmentBinding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        profileFragmentBinding.postRecycleView.adapter= postAdapter
        profileViewModel.dataProfile.observe(viewLifecycleOwner){
            profileFragmentBinding.login.text = String.format(getString(R.string.login),it.login)
            profileFragmentBinding.name.text = String.format(getString(R.string.name),it.name)
            val animPlaceHolder =  requireContext().getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
            animPlaceHolder.start()// probably needed
            Glide.with(requireContext()).load(it.avatar).circleCrop().placeholder(animPlaceHolder).timeout(10_000).error(R.drawable.null_avatar).into(profileFragmentBinding.profileAvatar)
        }
        profileViewModel.dataState.observe(viewLifecycleOwner){
            profileFragmentBinding.progressBarLayout.isVisible = it.loading
            it.error?.let{
                errorCallback.onError(it.reason,it.onRetryListener)
            }
        }
        profileFragmentBinding.exit.setOnClickListener {
            profileViewModel.exit {
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest {
                    postAdapter.submitData(it)
                    profileFragmentBinding.emptyMyPostListText.isVisible = postAdapter.itemCount ==0
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.auth.authStateFlow.collectLatest {
                    postAdapter.refresh()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    profileFragmentBinding.postSwipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        profileFragmentBinding.postSwipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        return profileFragmentBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(login: String, name: String,avatar:String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(LOGIN, login)
                    putString(NAME, name)
                    putString(AVATAR,avatar)
                }
            }
    }
}