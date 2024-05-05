package usecase

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.flow
import repository.UserRepository
import javax.inject.Inject


class UserUsecase @Inject constructor(private val userRepository: UserRepository){
    suspend fun updateProfile(nickName: String, profile: Uri) = flow {
        emit(userRepository.updateProfile(nickName,profile))
    }
    suspend fun getUserProfile(context : Context) = flow{
        emit(userRepository.getUserProfile(context))
    }
}