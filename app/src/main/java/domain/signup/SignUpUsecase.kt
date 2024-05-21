package domain.signup

import android.content.Context
import kotlinx.coroutines.flow.flow
import data.repository.signup.SignUpActivityRepository
import javax.inject.Inject

class SignUpUsecase @Inject constructor(private val signUpActivityRepository: SignUpActivityRepository){
    suspend fun setUserNickname (userNickname: String) = signUpActivityRepository.setUserNickname(userNickname)
    suspend fun saveUserLoginInfo(email : String, passwd : String, context : Context) = flow{
        emit(signUpActivityRepository.saveUserLoginInfo(email, passwd, context))
    }
}