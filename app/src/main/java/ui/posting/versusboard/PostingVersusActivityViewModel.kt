package ui.posting.versusboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Response
import domain.versus.PostVersusBoardUsecase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostingVersusActivityViewModel @Inject constructor(private val postVersusBoardUsecase : PostVersusBoardUsecase) :
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
        postVersusBoardUsecase(boardTitle, opinion1, opinion2, writerUID).collect{
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