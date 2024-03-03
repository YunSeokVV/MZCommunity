package viewModel

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import useCase.LoginActivityUseCase
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val loginActivityUseCase: LoginActivityUseCase) :
    ViewModel() {

    fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        loginActivityUseCase.signInWithGoogle(completedTask)
    }
}