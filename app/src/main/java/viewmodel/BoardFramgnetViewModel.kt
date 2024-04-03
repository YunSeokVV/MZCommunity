package viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.DailyBoard
import model.Response
import usecase.DailyBoardUseCase
import javax.inject.Inject

@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase) : ViewModel(){

    private val _documents = MutableLiveData<List<DailyBoard>>()

    init {
        getDailyBoards()
    }

    fun increaseDailyBoardLike(dailyBoard: DailyBoard, documentId : String, adapterPosition : Int) = viewModelScope.launch {
        dailyBoardUseCase.increaseDailyBoardLikeUseCase(dailyBoard).collect {
            when (it) {
                is Response.Success -> {
                    getDailyBoard(documentId, adapterPosition)
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun increaseDailyBoardDisLike(dailyBoard: DailyBoard, documentId : String, adapterPosition : Int) = viewModelScope.launch {
        dailyBoardUseCase.increaseDailyBoardDisLike(dailyBoard).collect {
            when (it) {
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
            _documents.value = it
        }
    }

    fun getDailyBoard(documentId : String, adapterPosition : Int) = viewModelScope.launch {
        dailyBoardUseCase.getDailyBoard(documentId).collect{
            val updateList = _documents.value?.toMutableList() ?: mutableListOf()
            updateList.set(adapterPosition, it)
            _documents.value = updateList
        }
    }

}