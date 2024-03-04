package viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import useCase.LoginActivityUseCase
import javax.inject.Inject
import com.orhanobut.logger.Logger
@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val loginActivityUseCase: LoginActivityUseCase) :
    ViewModel() {

    private val _isGoogleSignIn = MutableLiveData<Boolean>(false)

    val isGoogleLogin : LiveData<Boolean>
        get() = _isGoogleSignIn

    suspend fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) {

            loginActivityUseCase.signInWithGoogle(completedTask).collect{
                _isGoogleSignIn.value = it
                Logger.v(it.toString())
            }
    }

}