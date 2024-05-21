package ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Response
import domain.login.SignInUsecase
import javax.inject.Inject
import domain.signup.SignUpUsecase

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val application: Application,
    private val signInUsecase: SignInUsecase,
    private val signUpUsecase: SignUpUsecase
) : AndroidViewModel(application) {
    init {
        getSavedUserLoginInfo(application.applicationContext)
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
        signInUsecase.signInWithGoogle(completedTask).collect {
            _isGoogleSignIn.value = it
            val account = completedTask.getResult(ApiException::class.java)
            setUserNickName(account.email.toString())
        }
    }

    private suspend fun setUserNickName(nickName: String) {
        signUpUsecase.setUserNickname(nickName)
    }

    // 이메일로 로그인한 사용자의 정보를 불러오는 메소드
    private fun getSavedUserLoginInfo(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        signInUsecase.getSavedUserLoginInfo(context).collect {
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