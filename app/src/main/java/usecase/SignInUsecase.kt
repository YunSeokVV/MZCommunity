package usecase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.logger.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import repository.SignInActivityRepository
import util.FirebaseAuth
import javax.inject.Inject

class SignInUsecase @Inject constructor(private val signInActivityRepository: SignInActivityRepository) {
    fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) = callbackFlow {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val mAuth = FirebaseAuth.auth

            val credential: AuthCredential =
                GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        GlobalScope.launch {
                            trySend(it.isSuccessful)
                        }
                    }
                }

        } catch (e: ApiException) {
            Logger.v(e.message.toString())
            Logger.v(e.statusCode.toString())

        }
        awaitClose()
    }

    suspend fun getSavedUserLoginInfo(context: Context) = flow {
        emit(signInActivityRepository.getSavedUserLoginInfo(context))
    }

}