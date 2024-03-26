package viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.DailyBoard
import useCase.DailyBoardUseCase
import javax.inject.Inject

@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase, private val increaseDailyBoardLikeUseCase: IncreaseDailyBoardLikeUseCase) : ViewModel(){

    private val _documents = MutableLiveData<List<DailyBoard>>()

    init {
        getDailyBoard()
    }

//    fun increaseDailyBoardLike(increaseDailyBoardLikeUseCase: IncreaseDailyBoardLikeUseCase) = viewModelScope.launch {
//        increaseDailyBoardLikeUseCase(contetns, uploadImagesUri).collect {
//            when (it) {
//                is Response.Success -> {
//                    _isPostingComplete.value = it.data?:false
//                    Logger.v(_isPostingComplete.value.toString())
//                }
//
//                is Response.Failure -> {
//                    Logger.v(it.e?.message.toString())
//                }
//            }
//        }
//    }

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

    fun getDailyBoard() = viewModelScope.launch {

        dailyBoardUseCase.getDailyBoard().collect{
            _documents.value = it
        }

//        getDailyBoardUseCase().collect{
//            Logger.v(it.toString())
//
//            //todo : 둘중 맞는걸로 선택해서 해야함
//            //_documents.postValue(it)
//            _documents.value = it
//        }
    }

}