package ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Comment
import data.model.Response
import kotlinx.coroutines.launch
import domain.comment.GetCommentUsecase
import domain.comment.GetMoreCommentsUsecase
import domain.comment.GetNestedCommentsUsecase
import domain.comment.PostCommentUsecase
import domain.comment.PostReplyUsecase


import javax.inject.Inject

@HiltViewModel
class BottomSheetFragmentViewModel @Inject constructor(
    private val getMoreCommentsUsecase: GetMoreCommentsUsecase,
    private val postCommentUsecase: PostCommentUsecase,
    private val postReplyUsecase: PostReplyUsecase,
    private val getCommentUsecase: GetCommentUsecase,
    private val getNestedCommentsUsecase: GetNestedCommentsUsecase
) :
    ViewModel() {

    var showProgress: Boolean = false

    var isProgressLoading = MutableLiveData<Boolean>(true)

    // 사용자가 답글을 쓰는 것인지 댓글을 쓰는 것인지 판별해주는 변수
    var isReplyMode = false

    // 부모 댓글의 UID값이 담긴다. 대댓글을 쓰기위해 필요.
    var choosenReplyUID = String()

    // 대댓글을 쓰기 위해 태그한 사용자
    var tagedUser = String()

    private val _isPostingComplte = MutableLiveData<Boolean>()
    val isPostingComplete: LiveData<Boolean>
        get() {
            return _isPostingComplte
        }
    val dailyBoardComments = MutableLiveData<MutableList<Comment>>()

    private val _nestedComments = MutableLiveData<List<Comment>>()
    val nestedComment: LiveData<List<Comment>>
        get() {
            return _nestedComments
        }

    fun getMoreComments(parentUID: String, collectionName: String) = viewModelScope.launch {
        getMoreCommentsUsecase.invoke(parentUID, collectionName).collect {
            isProgressLoading.value = false
            dailyBoardComments.value = it.toMutableList()
            showProgress = false
        }

    }

    fun postComment(contents: String, parentUID: String, collectionName: String) =
        viewModelScope.launch {
            postCommentUsecase(contents, parentUID, collectionName).collect {
                when (it) {
                    is Response.Success -> {
                        _isPostingComplte.value = it.data ?: false

                    }

                    is Response.Failure -> {
                        Logger.v(it.e?.message.toString())
                    }
                }
            }
        }

    fun postReply(
        contents: String,
        parentUID: String,
        nestedCommentCollection: String,
        commentName: String
    ) = viewModelScope.launch {
        postReplyUsecase(contents, parentUID, nestedCommentCollection, commentName)
            .collect {
                when (it) {
                    is Response.Success -> {
                        _isPostingComplte.value = it.data ?: false
                    }

                    is Response.Failure -> {
                        Logger.v(it.e?.message.toString())
                    }
                }
            }
    }

    fun getComments(parentUID: String, collectionName: String) = viewModelScope.launch {
        getCommentUsecase(parentUID, collectionName).collect {
            dailyBoardComments.value = it.toMutableList()
        }
    }

    fun getNestedComments(parentUID: String, nestedCommentName: String) = viewModelScope.launch {
        getNestedCommentsUsecase(parentUID, nestedCommentName).collect {
            _nestedComments.value = it
        }
    }


}