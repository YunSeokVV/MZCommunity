package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _dailyBoardComments = MutableLiveData<List<Comment>>()

    val isPostingComplete: LiveData<Boolean>
        get() {
            return _isPostingComplte
        }

    private val _nestedComments = MutableLiveData<List<Comment>>()
    val nestedComment: LiveData<List<Comment>>
        get() {
            return _nestedComments
        }

    val dailyBoardComments: LiveData<List<Comment>>
        get() {
            return _dailyBoardComments
        }

    fun setParentUID(uid: String) {
        _parentUID.value = uid
    }

    fun postComment(contents: String, parentUID: String, collectionName: String) =
        viewModelScope.launch {
            commentUseCase.postComment(contents, parentUID, collectionName).collect {
                when (it) {
                    is Response.Success -> {
                        _isPostingComplte.value = it.data ?: false
                        getComments(parentUID, collectionName)
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
            Logger.v(it.toString())
            _dailyBoardComments.value = it
            _isPostingComplte.value = true
        }
    }

    fun getNestedComments(parentUID: String, nestedCommentName : String) = viewModelScope.launch {
        commentUseCase.getNestedComments(parentUID, nestedCommentName).collect {
            _nestedComments.value = it
        }
    }


}