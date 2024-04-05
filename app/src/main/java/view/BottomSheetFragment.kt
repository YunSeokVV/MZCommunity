package view


import adapter.DailyBoardCommentAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnKeyListener
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.Comment
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
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
                // 대댓글을 쓰는 경우
                if(viewModel.isReplyMode){
                    val reply = Util.removeStr(inputComment.text.toString(), viewModel.tagedUser)
                    viewModel.postReply(reply, viewModel.choosenReplyUID)
                }
                // 댓글을 쓰는 경우
                else{
                    viewModel.postDailyComment(inputComment.text.toString(), parentUID)
                }
            }
        }


        binding.comment.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        viewModel.dailyBoardComments.observe(this, Observer {
            adapter = DailyBoardCommentAdapter(it, object : DailyBoardCommentAdapter.PostReplyOnClickListener{
                override fun postReplyClick(comment : Comment) {
                    viewModel.choosenReplyUID = comment.commentUID
                    val userNickName = comment.writerName
                    inputComment.requestFocus()
                    imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT)
                    inputComment.setText("$userNickName")
                    val spannableStringBuilder = SpannableStringBuilder(userNickName)
                    spannableStringBuilder.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.theme)),
                        0,
                        inputComment.text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    inputComment.text = spannableStringBuilder
                    viewModel.tagedUser = inputComment.text.toString()
                    inputComment.append(" ")
                    inputComment.setSelection(inputComment.length())
                    viewModel.isReplyMode = true

                }

            })
            binding.comment.adapter = adapter
            binding.comment.visibility = View.VISIBLE
            binding.loadingText.visibility = View.GONE
        })

        // 백스페이스를 눌러서 태그한 사용자를 지우는 경우 대댓글 모드 해제
        inputComment.setOnKeyListener { v, keyCode, event ->
            if(viewModel.isReplyMode){
                if(keyCode == KeyEvent.KEYCODE_DEL){
                    val isRreplyMode = viewModel.tagedUser.length
                    if(inputComment.text.length < isRreplyMode){
                        imm.hideSoftInputFromWindow(inputComment.windowToken, 0)
                        inputComment.setText("")
                        viewModel.isReplyMode = false
                    }
                }
            }
            false
        }

        return binding.root
    }
}