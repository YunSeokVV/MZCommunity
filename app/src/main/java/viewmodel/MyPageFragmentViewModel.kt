package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.User
import usecase.MypageUsecasae
import javax.inject.Inject

@HiltViewModel
class MyPageFragmentViewModel @Inject constructor(private val mypageUsecasae: MypageUsecasae) : ViewModel() {
    init {
        getUserProfile()
    }

    private val _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() {
            return _user
        }

    private val _isEditMode = MutableLiveData<Boolean>(false)

    val isEditMode : LiveData<Boolean>
        get() {
            return _isEditMode
        }

    fun setEditMode(){
        val currentValue = _isEditMode.value ?: false
        _isEditMode.value = !currentValue
    }

    fun getUserProfile() = viewModelScope.launch {
        mypageUsecasae.getUserProfile().collect{
            _user.value = it
        }
    }

}