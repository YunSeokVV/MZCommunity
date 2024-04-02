package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import usecase.GoogleLoginActivityUseCase
import usecase.SetUserNicknameUseCase

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val googleLoginActivityUseCase: GoogleLoginActivityUseCase, private val setUserNicknameUseCase : SetUserNicknameUseCase) :
    ViewModel() {

    private val _isGoogleSignIn = MutableLiveData<Boolean>(false)

    val isGoogleLogin: LiveData<Boolean>
        get() = _isGoogleSignIn

    suspend fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        googleLoginActivityUseCase.signInWithGoogle(completedTask).collect {
            _isGoogleSignIn.value = it
            val account = completedTask.getResult(ApiException::class.java)
            setUserNickName(account.email.toString())
        }
    }

    suspend fun setUserNickName(nickName : String){
        setUserNicknameUseCase(nickName)
    }

}