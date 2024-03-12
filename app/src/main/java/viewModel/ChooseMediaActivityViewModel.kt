package viewModel

import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import model.Images
import javax.inject.Inject

@HiltViewModel
class ChooseMediaActivityViewModel @Inject constructor() : ViewModel() {
    private val _isChoosen = MutableLiveData<Boolean>(false)

    private val picturedImageUri = mutableListOf<Images>()

    val isChoosen: LiveData<Boolean>
        get() {
            return _isChoosen
        }

    fun setChoosen(isChoosen: Boolean) {
        _isChoosen.value = isChoosen
        Logger.v(_isChoosen.value.toString())
    }

    fun setPicturedImageUri(image : Images) : List<Images>{
        picturedImageUri.add(image)
        return picturedImageUri
    }

}