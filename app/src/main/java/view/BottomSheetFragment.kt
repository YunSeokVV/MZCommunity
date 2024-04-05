package view


import adapter.DailyBoardCommentAdapter
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import util.Util
import viewmodel.BottomSheetFragmentViewModel


@AndroidEntryPoint
class BottomSheetFragment(private val parentUID : String) : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomSheetBinding
    private val viewModel by viewModels<BottomSheetFragmentViewModel>()
    private lateinit var adapter : DailyBoardCommentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater)
        val inputComment = binding.inputComment
        val progressDialog = ProgressDialog(requireContext())


        viewModel.setParentUID(parentUID)
        viewModel.parentUID.observe(this, Observer {
            Logger.v(it)
            viewModel.getDailyComments(it)
        })

        viewModel.isPostingComplete.observe(this, Observer { data ->
            if(data){
                inputComment.setText("")
                progressDialog.dismiss()
            }
        })

        binding.postComment.setOnClickListener {
            if(inputComment.text.toString().isEmpty()){
                Util.makeToastMessage("댓글을 입력해주세요!", requireContext())
            } else{
                progressDialog.show()
                viewModel.postDailyComment(inputComment.text.toString(), parentUID)
            }
        }


        binding.comment.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        viewModel.dailyBoardComments.observe(this, Observer {
            Logger.v(it.toString())
            adapter = DailyBoardCommentAdapter(it, object : DailyBoardCommentAdapter.PostReplyOnClickListener{
                override fun postReplyClick(userNickName: String) {
                    inputComment.requestFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT)
                    inputComment.setText("@$userNickName")
                    val spannableStringBuilder = SpannableStringBuilder(userNickName)
                    spannableStringBuilder.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.theme)),
                        0,
                        inputComment.text.length-1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    inputComment.text = spannableStringBuilder
                    inputComment.append(" ")
                    inputComment.setSelection(inputComment.length())
                }

            })
            binding.comment.adapter = adapter
            binding.comment.visibility = View.VISIBLE
            binding.loadingText.visibility = View.GONE
        })


        return binding.root
    }
}