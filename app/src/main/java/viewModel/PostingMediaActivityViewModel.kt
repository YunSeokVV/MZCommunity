package viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.Images
import model.Response
import javax.inject.Inject
import model.Response.Success
import useCase.DailyBoardUseCase

@HiltViewModel
class PostingMediaActivityViewModel @Inject constructor(private val dailyBoardUseCase: DailyBoardUseCase) :
    ViewModel() {

    private val _isPostingComplete = MutableLiveData<Boolean>()

    val isPostingComplete: LiveData<Boolean>
        get() {
            return _isPostingComplete
        }

    fun createPost(contetns: String, uploadImagesUri: ArrayList<Images>) = viewModelScope.launch {
        dailyBoardUseCase.postDailyBoardRepository(contetns, uploadImagesUri).collect {
            when (it) {
                is Response.Success -> {
                    _isPostingComplete.value = it.data?:false
                    Logger.v(_isPostingComplete.value.toString())
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }


}