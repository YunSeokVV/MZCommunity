package view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentMyPageBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.LoginedUser
import util.Util
import viewmodel.MyPageFragmentViewModel

@AndroidEntryPoint
class MyPageFragment(private val loginedUserProfile: LoginedUser) : Fragment() {


    interface loginUserListener {
        fun loginUserListner(loginedUser: LoginedUser)
    }

    private lateinit var loginedUser: loginUserListener
    private val viewModel: MyPageFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMyPageBinding
    private var profileUri: String = Util.getUnknownProfileImage()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let {
                Glide.with(this).load(it).into(binding.userProfileImg)
                profileUri = it.toString()
            }

        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            loginedUser = activity as loginUserListener
        } catch (e: Exception) {
            Logger.v(e.message.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        binding.editProfile.setOnClickListener {
            viewModel.setEditMode()
        }
        binding.userProfileImg.setOnClickListener {
            startForResult.launch("image/*")
        }

        viewModel.isEditMode.observe(requireActivity(), Observer<Boolean> { data ->
            if (data) {
                binding.editProfile.text = getString(R.string.complete)
                binding.camera.visibility = View.VISIBLE
                binding.pencil.visibility = View.VISIBLE
                binding.arrowBack.visibility = View.VISIBLE
                binding.editInfo.visibility = View.VISIBLE
                binding.userProfileImg.isEnabled = true
                binding.userName.isEnabled = true

                binding.editProfile.setOnClickListener {
                    Util.showProgressDialog(requireContext(), true)
                    viewModel.updateProfile(binding.userName.text.toString(), Uri.parse(profileUri))
                    viewModel.setEditMode()
                }

            } else {
                binding.editProfile.text = getString(R.string.edit)
                binding.camera.visibility = View.INVISIBLE
                binding.pencil.visibility = View.INVISIBLE
                binding.arrowBack.visibility = View.INVISIBLE
                binding.editInfo.visibility = View.INVISIBLE
                binding.userProfileImg.isEnabled = false
                binding.userName.isEnabled = false

            }
        })

        viewModel.loginedUser.observe(requireActivity(), Observer {
            loginedUser.loginUserListner(it)
            Util.showProgressDialog(requireContext(), false)
        })

        Glide.with(requireContext()).load(Uri.parse(loginedUserProfile.profileUri))
            .into(binding.userProfileImg)
        binding.userName.setText(loginedUserProfile.nickName)

        return binding.root
    }

}