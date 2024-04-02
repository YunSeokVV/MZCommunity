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
import model.Response
import usecase.DailyCommentUseCase
import javax.inject.Inject

@HiltViewModel
class BottomSheetFragmentViewModel @Inject constructor(private val dailyCommentUseCase: DailyCommentUseCase) :ViewModel() {

    private val _isPostingComplte = MutableLiveData<Boolean>()

    val isPostingComplete : LiveData<Boolean>
        get() {
            return _isPostingComplte
        }

    fun postDailyCommentRepository(contents: String) = viewModelScope.launch {
        dailyCommentUseCase.postDailyCommentRepository(contents).collect{
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

}