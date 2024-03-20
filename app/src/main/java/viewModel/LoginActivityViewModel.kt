package viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collect
import useCase.GoogleLoginActivityUseCase
import useCase.SetUserNicknameUseCase

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