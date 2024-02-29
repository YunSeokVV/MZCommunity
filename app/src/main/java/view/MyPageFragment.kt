package view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentMyPageBinding
import com.orhanobut.logger.Logger
import android.Manifest
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import util.FirebaseAuth
import viewModel.MyPageFragmentViewModel
import java.security.AccessController.checkPermission

class MyPageFragment : Fragment() {
    private val myPageFragmentViewModel: MyPageFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMyPageBinding

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { Glide.with(this).load(it).into(binding.userProfileImg) }
            it?.let { uri -> uploadtMyProfile(uri) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(layoutInflater)
        binding.editProfile.setOnClickListener {
            myPageFragmentViewModel.setEditMode()
        }

        Logger.v(FirebaseAuth.auth.uid.toString())

        binding.userProfileImg.setOnClickListener {
            startForResult.launch("image/*")
        }

        myPageFragmentViewModel.isEditMode.observe(requireActivity(), Observer<Boolean> { data ->
            if (data) {
                binding.editProfile.text = getString(R.string.complete)
                binding.camera.visibility = View.VISIBLE
                binding.pencil.visibility = View.VISIBLE
                binding.arrowBack.visibility = View.VISIBLE
                binding.editInfo.visibility = View.VISIBLE
                binding.userProfileImg.isEnabled = true
                binding.userName.isEnabled = true
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


        return binding.root
    }

    fun uploadtMyProfile(uri : Uri){
        val storage = Firebase.storage
        var storageRef = storage.reference

        var userProfile = storageRef.child("user_profile_image/userProfile.jpg")
        val uploadTask = userProfile.putFile(uri)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Logger.v("upload failed ")

        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Logger.v("upload complete")
        }
    }

}