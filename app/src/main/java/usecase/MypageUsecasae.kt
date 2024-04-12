package usecase

import android.net.Uri
import kotlinx.coroutines.flow.flow
import repository.MyPageRepository
import javax.inject.Inject


class MypageUsecasae @Inject constructor(private val mypageRepository : MyPageRepository){
    suspend fun updateProfile(nickName: String, profile: Uri) = flow {
        emit(mypageRepository.updateProfile(nickName,profile))
    }
    suspend fun getUserProfile() = flow{
        emit(mypageRepository.getUserProfile())
    }
}