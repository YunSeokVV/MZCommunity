package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.User
import usecase.UserUsecase
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(private val userUsecase : UserUsecase) : ViewModel(){
    private val _userInfo = MutableLiveData<User>()
    val userInfo : LiveData<User>get() = _userInfo

    init {
        viewModelScope.launch {

            userUsecase.getUserProfile().collect{user ->
                _userInfo.value = user
            }
        }
    }
}