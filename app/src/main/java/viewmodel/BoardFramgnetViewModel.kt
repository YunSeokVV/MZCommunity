package viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlinx.coroutines.launch
import model.DailyBoard
import model.Response
import usecase.DailyBoardUseCase


@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase) : ViewModel(){

    private val _documents = MutableLiveData<List<DailyBoard>>()

    init {
        getDailyBoards()
    }

    // isLike : true인 경우 좋아요 버튼을 눌렀을때를 의미한다. false 인 경우에는 disLike 버튼을 눌렀다는 것을 의미
    fun increaseFavourability(dailyBoard: DailyBoard, documentId : String, adapterPosition : Int, isLike : Boolean) = viewModelScope.launch {
        dailyBoardUseCase.increaseFavourability(dailyBoard, isLike).collect{
            when(it){
                is Response.Success -> {
                    getDailyBoard(documentId, adapterPosition)
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    val document: LiveData<List<DailyBoard>>
        get() {
            return _documents

        }

    fun getDailyBoards() = viewModelScope.launch {
        dailyBoardUseCase.getDailyBoards().collect{
            Logger.v(it.toString())
            _documents.value = it
        }
    }

    fun getDailyBoard(documentId : String, adapterPosition : Int) = viewModelScope.launch {
        dailyBoardUseCase.getDailyBoard(documentId).collect{
            Logger.v(it.toString())
            val updateList = _documents.value?.toMutableList() ?: mutableListOf()
            updateList.set(adapterPosition, it)
            _documents.value = updateList
        }
    }

}