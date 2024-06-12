package domain.user

import android.net.Uri
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateProfileUsecase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(nickName: String, profile: Uri) = flow {
        emit(userRepository.updateProfile(nickName,profile))
    }
}