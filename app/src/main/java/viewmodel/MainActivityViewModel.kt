package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.LoginedUser
import usecase.UserUsecase
import javax.inject.Inject


@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val application: Application,
    private val userUsecase: UserUsecase
) : AndroidViewModel(application) {
    private val _Logined_userInfo = MutableLiveData<LoginedUser>()
    val loginedUserInfo: LiveData<LoginedUser> get() = _Logined_userInfo

    init {
        viewModelScope.launch {

            userUsecase.getUserProfile(application.applicationContext).collect { user ->
                _Logined_userInfo.value = user
            }
        }
    }
}