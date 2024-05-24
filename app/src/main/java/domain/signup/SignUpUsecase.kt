package domain.signup

import android.content.Context
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
class SignUpUsecase @Inject constructor(private val signUpActivityRepository: SignUpActivityRepository){
    suspend fun setUserNickname (userNickname: String) = signUpActivityRepository.setUserNickname(userNickname)
    suspend fun saveUserLoginInfo(email : String, passwd : String) = flow{
        emit(signUpActivityRepository.saveUserLoginInfo(email, passwd))
    }
}