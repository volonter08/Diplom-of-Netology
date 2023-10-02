package com.example.netologyandroidhomework1.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.netologyandroidhomework1.R
import com.example.netologyandroidhomework1.activity.TypeOfOperationForStartNewActivity
import com.example.netologyandroidhomework1.databinding.FragmentSavePostBinding
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.viewModel.PostViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavePostFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSavePostBinding.inflate(layoutInflater)
        val editedPost = arguments?.getSerializable("post") as Post?
        val editText = binding.editText.apply {
            text.clear()
        }
        val button = binding.save
        val cancelButton = binding.cancelButton
        if (editedPost == null) {
            button.setIconResource(R.drawable.baseline_create_24)
            button.setText("CREATE")
            binding.linearLayoutUpdate.visibility = View.GONE
            val newIntent = Intent()
            button.setOnClickListener {
                if (editText.text.isBlank())
                    Snackbar.make(binding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){
                    }.show()
                else {
                    viewModel.createPost(editText.text.toString())
                    findNavController().navigateUp()
                }
            }
        } else {
            button.setIconResource(R.drawable.baseline_save_as_24)
            button.text = "EDIT"
            binding.updateContentText.text = editedPost.content
            editText.setText(editedPost.content)
            binding.linearLayoutUpdate.visibility = View.VISIBLE
            button.setOnClickListener {
                if (editText.text.isBlank()) {
                    Snackbar.make(binding.root,"Контент не может быть пустым",Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok){
                    }.show()
                } else {
                    viewModel.update(editedPost.copy(content = editText.text.toString()))
                    findNavController().navigateUp()
                }
            }
            cancelButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        return binding.root
    }
}