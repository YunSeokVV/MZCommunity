package ui.comment


import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import data.model.Comment

import data.model.LoginedUser
import ui.loading.LoadingDialogFragment

import util.Util


@AndroidEntryPoint
class BottomSheetFragment() : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private val viewModel by viewModels<BottomSheetFragmentViewModel>()
    private lateinit var adapter: CommentAdapter
    private lateinit var nestedRecyclerView: RecyclerView
    private val loadingDialogFragment = LoadingDialogFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater)
        val inputComment = binding.inputComment
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        requireActivity().getWindow()
            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)

        val parentUID = arguments?.getString("boardUID") ?: requireActivity().getString(R.string.nothing)
        val collectionName =
            arguments?.getString("collectionName") ?: requireActivity().getString(R.string.nothing)

        val nestedCommentCollection =
            arguments?.getString("nestedCommentName") ?: requireActivity().getString(R.string.nothing)
        val commentName = arguments?.getString("commentName") ?: requireActivity().getString(R.string.nothing)
        val loginedUser = arguments?.getSerializable("loginedUserProfile") as LoginedUser

        viewModel.getComments(parentUID, collectionName)

        viewModel.isPostingComplete.observe(this, Observer { data ->
            if (data) {
                if (viewModel.isReplyMode) {
                    adapter.addNestedCommentItem(
                        loginedUser,
                        removeStr(inputComment.text.toString(), viewModel.tagedUser),
                        parentUID
                    )
                    nestedRecyclerView.adapter?.notifyItemInserted(adapter.nestedCommentList.size)
                    viewModel.isReplyMode = false
                } else {
                    adapter.addComment(loginedUser, inputComment.text.toString(), parentUID)
                }
                inputComment.setText("")
                loadingDialogFragment.dismiss()
            }
        })

        binding.postComment.setOnClickListener {
            if (inputComment.text.toString().isEmpty()) {
                Util.makeToastMessage("댓글을 입력해주세요!", requireContext())
            } else {
                loadingDialogFragment.show(childFragmentManager, loadingDialogFragment.tag)
                // 대댓글을 쓰는 경우
                if (viewModel.isReplyMode) {
                    val reply = removeStr(inputComment.text.toString(), viewModel.tagedUser)
                    viewModel.postReply(
                        reply,
                        viewModel.choosenReplyUID,
                        nestedCommentCollection,
                        commentName
                    )
                }
                // 댓글을 쓰는 경우
                else {
                    viewModel.postComment(inputComment.text.toString(), parentUID, collectionName)
                }
            }
        }


        binding.comment.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        adapter = CommentAdapter(
            object : CommentAdapter.PostReplyOnClickListener {
                override fun postReplyClick(comment: Comment, recyclerView: RecyclerView) {
                    nestedRecyclerView = recyclerView
                    setTagedUser(comment)
                }

            },
            object : CommentAdapter.ShowCommentListener {
                override fun showNestedComment(comment: Comment, recyclerView: RecyclerView) {
                    nestedRecyclerView = recyclerView
                    val parentUID = comment.commentUID
                    viewModel.getNestedComments(comment.commentUID, nestedCommentCollection)
                    viewModel.nestedComment.observe(
                        requireActivity(),
                        Observer { nestedComments ->
                            adapter.setNestedCommentsList(nestedComments)
                            val nestedCommentAdapter = NestedCommentAdapter(
                                adapter.nestedCommentList,
                                object :
                                    NestedCommentAdapter.PostReplyOnClickListener {
                                    override fun postReplyClick(comment: Comment) {
                                        setTagedUser(comment, parentUID)
                                    }

                                })
                            nestedRecyclerView.adapter = nestedCommentAdapter
                        })
                }

            })


        // 리사이클러뷰 초기설정
        binding.comment.adapter = adapter
        binding.comment.visibility = View.VISIBLE
        binding.comment.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.comment.canScrollVertically(1)) {
                    if (!viewModel.showProgress) {
                        viewModel.showProgress = true
                        val linearLayoutManager: LinearLayoutManager =
                            binding.comment.layoutManager as LinearLayoutManager
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                            adapter.showProgress()
                            viewModel.getMoreComments(
                                parentUID,
                                collectionName
                            )
                        }
                    }
                }

            }
        })


        viewModel.isProgressLoading.observe(this, Observer { isProgressLoading ->
            if (!isProgressLoading) {

                adapter.hideProgress()
            }
        })

        viewModel.dailyBoardComments.observe(this, Observer {
            adapter.comments.addAll(it)
            adapter.notifyItemRangeInserted(it.size, it.size)
            binding.loadingText.visibility = View.GONE
        })

        // 백스페이스를 눌러서 태그한 사용자를 지우는 경우 대댓글 모드 해제
        inputComment.setOnKeyListener { v, keyCode, event ->
            if (viewModel.isReplyMode) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    val isRreplyMode = viewModel.tagedUser.length
                    if (inputComment.text.length < isRreplyMode) {
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

    fun setTagedUser(comment: Comment) {
        viewModel.choosenReplyUID = comment.commentUID
        val inputComment = binding.inputComment
        val userNickName = comment.writerName
        inputComment.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT)
        inputComment.setText("$userNickName")
        val spannableStringBuilder = SpannableStringBuilder(userNickName)
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.theme)),
            0,
            inputComment.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inputComment.text = spannableStringBuilder
        viewModel.tagedUser = inputComment.text.toString()
        inputComment.append(" ")
        inputComment.setSelection(inputComment.length())
        viewModel.isReplyMode = true
    }

    fun setTagedUser(comment: Comment, parentUID: String) {
        viewModel.choosenReplyUID = parentUID
        val inputComment = binding.inputComment
        val userNickName = comment.writerName
        inputComment.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT)
        inputComment.setText("$userNickName")
        val spannableStringBuilder = SpannableStringBuilder(userNickName)
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.theme)),
            0,
            inputComment.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inputComment.text = spannableStringBuilder
        viewModel.tagedUser = inputComment.text.toString()
        inputComment.append(" ")
        inputComment.setSelection(inputComment.length())
        viewModel.isReplyMode = true
    }

    private fun removeStr(original: String, deleteStr: String): String {
        var result = original
        result = result.substring(deleteStr.length)
        return result
    }
}