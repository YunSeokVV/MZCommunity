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
        }
    }


}