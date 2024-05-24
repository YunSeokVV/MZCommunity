package domain.loading

import android.content.Context
import kotlinx.coroutines.flow.flow
import model.DailyBoard
import model.LoginedUser
import javax.inject.Inject

class LoadingUsecase @Inject constructor(private val loadingRepostiroy : LoadingRepository){

    suspend fun getUserProfile() = flow{
        emit(loadingRepostiroy.getUserProfile())
    }

    suspend fun getDailyBoards() = flow {
        emit(loadingRepostiroy.getDailyBoards())
    }
}