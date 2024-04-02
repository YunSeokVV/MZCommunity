package view


import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import util.Util
import viewmodel.BottomSheetFragmentViewModel

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomSheetBinding
    private val viewModel by viewModels<BottomSheetFragmentViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater)

        val progressDialog = ProgressDialog(requireContext())

        viewModel.isPostingComplete.observe(this, Observer { data ->
            if(data){
                binding.inputComment.setText("")
                progressDialog.dismiss()
            }
        })

        binding.postComment.setOnClickListener {
            if(binding.inputComment.text.toString().isEmpty()){
                Util.makeToastMessage("댓글을 입력해주세요!", requireContext())
            } else{
                progressDialog.show()
                viewModel.postDailyCommentRepository(binding.inputComment.text.toString())
            }

        }


        return binding.root
    }
}