package viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlinx.coroutines.launch
import model.DailyBoard
import model.Response
import usecase.DailyBoardUseCase


@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase) : ViewModel(){

    // 리사이클러뷰가 새로고침중인지 체크해주는 플래그값이다.
    var isRfreshing = false

    private val _dailyBoards = MutableLiveData<List<DailyBoard>>()

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

    val dailyBoards: LiveData<List<DailyBoard>>
        get() {
            return _dailyBoards

        }
    fun getDailyBoard(documentId : String, adapterPosition : Int) = viewModelScope.launch {
        dailyBoardUseCase.getDailyBoard(documentId).collect{
            val updateList = _dailyBoards.value?.toMutableList() ?: mutableListOf()
            updateList.set(adapterPosition, it)
            _dailyBoards.value = updateList
        }
    }

    fun initDailyBoards(dailyBoards : List<DailyBoard>){
        _dailyBoards.value = dailyBoards
    }

    fun getRandomDailyBoards() = viewModelScope.launch {
        dailyBoardUseCase.getRandomDailyBoards().collect{
            _dailyBoards.postValue(it)
        }
    }


}