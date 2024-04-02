package usecase

import repository.LoginActivityRepository
import javax.inject.Inject

class SetUserNicknameUseCase @Inject constructor(private val loginActivityRepository: LoginActivityRepository){
    suspend operator fun invoke(userNickname: String) = loginActivityRepository.setUserNickname(userNickname)
}