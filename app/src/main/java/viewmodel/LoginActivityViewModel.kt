package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import usecase.SignInUsecase
import javax.inject.Inject
import usecase.SignUpUsecase

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val signInUsecase: SignInUsecase, private val signUpUsecase: SignUpUsecase) :
    ViewModel() {

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

    private suspend fun setUserNickName(nickName : String){
        signUpUsecase.setUserNickname(nickName)
    }

    // 이메일로 로그인한 사용자의 정보를 불러오는 메소드
    private suspend fun getSavedUserLoginInfo(){

    }

}