package domain.signup


import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveUserLoginInfoUsecase @Inject constructor(private val signUpRepository : SignUpRepository){
    suspend operator fun invoke(email : String, passwd : String) = flow{
        emit(signUpRepository.saveUserLoginInfo(email, passwd))
    }
}