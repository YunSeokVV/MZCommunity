package viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import useCase.GetDailyBoardUseCase
import javax.inject.Inject

@HiltViewModel
class BoardFramgnetViewModel @Inject constructor(private val getDailyBoardUseCase : GetDailyBoardUseCase) : ViewModel(){

    private val _documents = MutableLiveData<String>()

    init {
        getDailyBoard()
    }

    val document: LiveData<String>
        get() {
            return _documents
        }

    fun getDailyBoard() = viewModelScope.launch {
        getDailyBoardUseCase().collect{
            Logger.v(it)
        }
    }

}