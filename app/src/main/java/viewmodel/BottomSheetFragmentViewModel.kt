package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import model.Comment
import model.Response
import usecase.DailyCommentUseCase
import javax.inject.Inject

@HiltViewModel
class BottomSheetFragmentViewModel @Inject constructor(private val dailyCommentUseCase: DailyCommentUseCase) :ViewModel() {

    // 사용자가 답글을 쓰는 것인지 댓글을 쓰는 것인지 판별해주는 변수
    var isReplyMode = false

    // 댓글의 UID값이 담긴다. 대댓글을 쓰기위해 필요.
    lateinit var choosenReplyUID : String

    // 대댓글을 쓰기 위해 태그한 사용자
    lateinit var tagedUser: String


    private val _parentUID = MutableLiveData<String>()
    val parentUID : LiveData<String>
        get() {
            return _parentUID
        }

    private val _isPostingComplte = MutableLiveData<Boolean>()

    private val _dailyBoardComments = MutableLiveData<List<Comment>>()

    val isPostingComplete : LiveData<Boolean>
        get() {
            return _isPostingComplte
        }

    val dailyBoardComments : LiveData<List<Comment>>
        get() {
            return _dailyBoardComments
        }


    init{
        //_parentUID.value?.let { getDailyComments(it) }
        Logger.v(_parentUID.value.toString())
    }

    fun setParentUID(uid : String){
        _parentUID.value = uid
    }

    fun postDailyComment(contents: String, parentUID : String) = viewModelScope.launch {
        dailyCommentUseCase.postDailyComment(contents, parentUID).collect{
            when(it){
                is Response.Success -> {
                    getDailyComments(parentUID)
                    //_isPostingComplte.value = it.data?:false
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun postReply(contents: String, parentUID: String) = viewModelScope.launch {
        dailyCommentUseCase.postReply(contents, parentUID).collect{
            when(it){
                is Response.Success -> {
                    _isPostingComplte.value = it.data?:false
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun getDailyComments(parentUID : String) = viewModelScope.launch {
        dailyCommentUseCase.getDailyComments(parentUID).collect{
            _dailyBoardComments.value = it
            _isPostingComplte.value = true
        }
    }


}