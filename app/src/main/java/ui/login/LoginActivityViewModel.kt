package ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Response
import domain.login.GetSavedUserLoginInfoUsecase
import domain.login.LoginGoogleUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import domain.signup.SetUserNicknameUsecase
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val getSavedUserLoginInfoUsecase: GetSavedUserLoginInfoUsecase,
    private val loginGoogleUsecase: LoginGoogleUsecase,
    private val setUserNicknameUsecase: SetUserNicknameUsecase,
) : ViewModel() {
    init {
        getSavedUserLoginInfo()
    }


    private val _emailAutoLogin = MutableLiveData<Boolean>()

    val emailAutoLogin: LiveData<Boolean>
        get() {
            return _emailAutoLogin
        }

    private val _isGoogleSignIn = MutableLiveData<Boolean>(false)

    val isGoogleLogin: LiveData<Boolean>
        get() = _isGoogleSignIn

    suspend fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        loginGoogleUsecase(completedTask).collect {
            _isGoogleSignIn.value = it
            val account = completedTask.getResult(ApiException::class.java)
            setUserNickName(account.email.toString())
        }
    }

    private suspend fun setUserNickName(nickName: String) {
        //signUpUsecase.setUserNickname(nickName)
        setUserNicknameUsecase.invoke(nickName)
    }

    // 이메일로 로그인한 사용자의 정보를 불러오는 메소드
    private fun getSavedUserLoginInfo() = viewModelScope.launch(Dispatchers.IO) {
        getSavedUserLoginInfoUsecase().collect {
            when (it) {
                is Response.Success -> {
                    if (it.data.loginWay == "email")
                        _emailAutoLogin.postValue(true)
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }
}