package view

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
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import util.FirebaseAuth
import viewmodel.MyPageFragmentViewModel

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private val viewModel: MyPageFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var profileUri : Uri
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { Glide.with(this).load(it).into(binding.userProfileImg) }
            profileUri = (it ?: R.drawable.user_profile2) as Uri
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
                    viewModel.updateProfile(binding.userName.text.toString(), profileUri)
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

        viewModel.user.observe(requireActivity(), Observer {
            Logger.v(it.nickName)
            profileUri = it.profileUri
            Glide.with(requireContext()).load(it.profileUri).into(binding.userProfileImg)
            it.nickName
            binding.userName.setText(it.nickName)
        })


        return binding.root
    }

}