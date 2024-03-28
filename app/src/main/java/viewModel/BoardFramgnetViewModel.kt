package viewModel

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
import useCase.DailyBoardUseCase
import javax.inject.Inject

@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase) : ViewModel(){

    private val _documents = MutableLiveData<List<DailyBoard>>()

    init {
        getDailyBoards()
    }

    fun increaseDailyBoardLike(dailyBoard: DailyBoard) = viewModelScope.launch {
        dailyBoardUseCase.increaseDailyBoardLikeUseCase(dailyBoard).collect {
            when (it) {
                is Response.Success -> {
                    getDailyBoards()
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun increaseDailyBoardDisLike(dailyBoard: DailyBoard) = viewModelScope.launch {
        dailyBoardUseCase.increaseDailyBoardDisLike(dailyBoard).collect {
            when (it) {
                is Response.Success -> {
                    getDailyBoards()
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun getUserUploadFilesUri(): List<List<Uri>> {
        var uris = mutableListOf<List<Uri>>()
        _documents.value?.forEach {
            uris.add(it.images)
        }

        return uris
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

}