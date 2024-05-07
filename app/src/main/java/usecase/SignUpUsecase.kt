package usecase

import android.content.Context
import androidx.lifecycle.viewModelScope
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
import repository.SignUpActivityRepository
import util.FirebaseAuth
import javax.inject.Inject

class SignUpUsecase @Inject constructor(private val signUpActivityRepository: SignUpActivityRepository){
    suspend fun setUserNickname (userNickname: String) = signUpActivityRepository.setUserNickname(userNickname)
    suspend fun saveUserLoginInfo(email : String, passwd : String, context : Context) = flow{
        emit(signUpActivityRepository.saveUserLoginInfo(email, passwd, context))
    }
}