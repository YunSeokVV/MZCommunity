package view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentMyPageBinding

import viewModel.MyPageFragmentViewModel

class MyPageFragment : Fragment() {
    private val myPageFragmentViewModel : MyPageFragmentViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyPageBinding.inflate(layoutInflater)

        binding.editProfile.setOnClickListener{
            myPageFragmentViewModel.setEditMode()
        }

        myPageFragmentViewModel.isEditMode.observe(requireActivity(), Observer<Boolean> { data ->
            if(data){
                binding.editProfile.text = getString(R.string.complete)
                binding.camera.visibility = View.VISIBLE
                binding.pencil.visibility = View.VISIBLE
            }
            else{
                binding.editProfile.text = getString(R.string.edit)
                binding.camera.visibility = View.INVISIBLE
                binding.pencil.visibility = View.INVISIBLE
            }
        })



        return binding.root
    }
}