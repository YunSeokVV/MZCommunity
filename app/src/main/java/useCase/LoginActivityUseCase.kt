package useCase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.orhanobut.logger.Logger
import util.FirebaseAuth
class LoginActivityUseCase (){
    fun signInWithGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val photoUrl = account.photoUrl.toString()
            Logger.v(photoUrl)

            val mAuth = FirebaseAuth.auth

            val credential: AuthCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                    }
                }

        } catch (e: ApiException) {
            Logger.v(e.message.toString())
            Logger.v(e.statusCode.toString())

        }
    }
}