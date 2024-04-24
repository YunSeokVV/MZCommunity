package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.Comment
import model.Response
import usecase.CommentUseCase
import javax.inject.Inject

@HiltViewModel
class BottomSheetFragmentViewModel @Inject constructor(private val commentUseCase: CommentUseCase) :
    ViewModel() {

    var showProgress: Boolean = false

    var isProgressLoading = MutableLiveData<Boolean>(true)

    // 사용자가 답글을 쓰는 것인지 댓글을 쓰는 것인지 판별해주는 변수
    var isReplyMode = false

    // 부모 댓글의 UID값이 담긴다. 대댓글을 쓰기위해 필요.
    lateinit var choosenReplyUID: String

    // 대댓글을 쓰기 위해 태그한 사용자
    lateinit var tagedUser: String

    private val _parentUID = MutableLiveData<String>()

    val parentUID: LiveData<String>
        get() {
            return _parentUID
        }

    private val _isPostingComplte = MutableLiveData<Boolean>()

    val dailyBoardComments = MutableLiveData<MutableList<Comment>>()

    val isPostingComplete: LiveData<Boolean>
        get() {
            return _isPostingComplte
        }

    private val _nestedComments = MutableLiveData<List<Comment>>()
    val nestedComment: LiveData<List<Comment>>
        get() {
            return _nestedComments
        }

    fun setParentUID(uid: String) {
        _parentUID.value = uid
    }

    fun getMoreComments(parentUID: String, collectionName: String) = viewModelScope.launch {
        commentUseCase.getMoreComments(parentUID, collectionName).collect {
            isProgressLoading.value = false
            dailyBoardComments.value = it.toMutableList()

            showProgress = false
        }

    }

    fun postComment(contents: String, parentUID: String, collectionName: String) =
        viewModelScope.launch {
            commentUseCase.postComment(contents, parentUID, collectionName).collect {
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
        commentUseCase.postReply(contents, parentUID, nestedCommentCollection, commentName)
            .collect {
                when (it) {
                    is Response.Success -> {
                        getNestedComments(choosenReplyUID, nestedCommentCollection)
                        _isPostingComplte.value = it.data ?: false
                    }

                    is Response.Failure -> {
                        Logger.v(it.e?.message.toString())
                    }
                }
            }
    }

    fun getComments(parentUID: String, collectionName: String) = viewModelScope.launch {
        commentUseCase.getComments(parentUID, collectionName).collect {
            dailyBoardComments.value = it.toMutableList()

        }
    }

    fun getNestedComments(parentUID: String, nestedCommentName: String) = viewModelScope.launch {
        commentUseCase.getNestedComments(parentUID, nestedCommentName).collect {
            _nestedComments.value = it
        }
    }


}