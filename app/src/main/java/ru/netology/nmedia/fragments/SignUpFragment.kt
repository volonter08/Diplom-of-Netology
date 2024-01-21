package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.viewModel.AuthViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val authViewModel:AuthViewModel by activityViewModels()
    @Inject
    lateinit var dataProfile:LiveData<Profile>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val signUpFragmentBinding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        signUpFragmentBinding.tryToSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        dataProfile.observe(viewLifecycleOwner){
            if (it.id != 0 || it.token != null) {
                findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
            }
        }
        signUpFragmentBinding.signUp.setOnClickListener {
            val login = signUpFragmentBinding.loginSignUp.text.toString()
            val password = signUpFragmentBinding.passwordSignUp.text.toString()
            val name = signUpFragmentBinding.nameSignUp.text.toString()
            when {
                name.isBlank()->Toast.makeText(requireContext(),"Name have to be not empty!",Toast.LENGTH_LONG,).show()
                login.isBlank() ->Toast.makeText(requireContext(),"Login have to be not empty!",Toast.LENGTH_LONG,).show()
                password.isBlank()->Toast.makeText(requireContext(),"Password have to be not empty!",Toast.LENGTH_LONG,).show()
                else-> authViewModel.signUp(login,password,name)
            }
        }
        return signUpFragmentBinding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}