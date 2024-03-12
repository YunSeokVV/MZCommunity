package viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageFragmentViewModel @Inject constructor() : ViewModel() {
    private val _isEditMode = MutableLiveData<Boolean>(false)

    val isEditMode : LiveData<Boolean>
        get() {
            return _isEditMode
        }

    fun setEditMode(){
        val currentValue = _isEditMode.value ?: false
        _isEditMode.value = !currentValue
    }

}