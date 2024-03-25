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
import useCase.GetDailyBoardUseCase
import javax.inject.Inject

@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val getDailyBoardUseCase : GetDailyBoardUseCase) : ViewModel(){

    private val _documents = MutableLiveData<List<DailyBoard>>()

    init {
        getDailyBoard()
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

    fun getDailyBoard() = viewModelScope.launch {
        getDailyBoardUseCase().collect{
            Logger.v(it.toString())

            //todo : 둘중 맞는걸로 선택해서 해야함
            //_documents.postValue(it)
            _documents.value = it
        }
    }

}