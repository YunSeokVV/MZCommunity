package domain.user

import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserProfileUsecase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke() = flow{
        emit(userRepository.getUserProfile())
    }
}