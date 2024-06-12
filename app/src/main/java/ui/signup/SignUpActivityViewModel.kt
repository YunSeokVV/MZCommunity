package ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Response
import domain.signup.SaveUserLoginInfoUsecase
import domain.signup.SetUserNicknameUsecase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpActivityViewModel @Inject constructor(
    private val setUserNicknameUsecase: SetUserNicknameUsecase,
    private val saveUserLoginInfoUsecase: SaveUserLoginInfoUsecase
) : ViewModel() {

    private val _saveUserInfo = MutableLiveData<Boolean>(false)
    val saveUserInfo: LiveData<Boolean> get() = _saveUserInfo

    suspend fun setUserNickName(nickName: String) {
        setUserNicknameUsecase((nickName))
    }

    suspend fun saveUserLoginInfo(email: String, passwd: String) = viewModelScope.launch {
        saveUserLoginInfoUsecase(email, passwd).collect {
            when (it) {
                is Response.Success -> {
                    _saveUserInfo.value = true
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }
}