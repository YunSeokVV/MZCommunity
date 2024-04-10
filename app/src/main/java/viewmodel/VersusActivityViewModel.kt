package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import model.Response
import usecase.VersusUsecase
import javax.inject.Inject

@HiltViewModel
class VersusActivityViewModel @Inject constructor(private val versusUsecase: VersusUsecase) :
    ViewModel() {

    private val _postingResponse = MutableLiveData<Boolean>(false)
    val postingResponse : LiveData<Boolean>
        get() {
            return _postingResponse
        }

    fun postVersusBoard(
        boardTitle: String,
        opinion1: String,
        opinion2: String,
        writerUID: String
    ) = viewModelScope.launch {
        versusUsecase.postVersusBoard(boardTitle, opinion1, opinion2, writerUID).collect{
            when(it){
                is Response.Success -> {
                    _postingResponse.value = it.data?:false
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

}