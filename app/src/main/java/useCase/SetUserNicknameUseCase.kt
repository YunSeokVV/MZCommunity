package useCase

import kotlinx.coroutines.flow.flow
import model.Images
import repository.LoginActivityRepository
import javax.inject.Inject

class SetUserNicknameUseCase @Inject constructor(private val loginActivityRepository: LoginActivityRepository){
    suspend operator fun invoke(userNickname: String) = loginActivityRepository.setUserNickname(userNickname)
}