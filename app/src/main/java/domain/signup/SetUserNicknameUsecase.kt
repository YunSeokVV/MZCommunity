package domain.signup

import javax.inject.Inject

class SetUserNicknameUsecase @Inject constructor(private val signUpRepository : SignUpRepository) {
    suspend operator fun invoke(userNickName : String) = signUpRepository.setUserNickname(userNickName)
}