package viewModel

import androidx.lifecycle.ViewModel
import com.example.mzcommunity.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import useCase.LoginActivityUseCase
import util.ConstValue
import util.FirebaseAuth
import javax.inject.Inject

// todo : useCase의 구현체를 만들어줘야 한다.
@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val loginActivityUseCase: LoginActivityUseCase) :
    ViewModel() {
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val photoUrl = account.photoUrl.toString()
            Logger.v(photoUrl)

            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Logger.v(e.message.toString())
            Logger.v(e.statusCode.toString())

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val mAuth = FirebaseAuth.auth
        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                }
            }

    }
}