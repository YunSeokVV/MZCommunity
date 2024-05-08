package usecase

import android.content.Context
import kotlinx.coroutines.flow.flow
import repository.LoadingRepository
import javax.inject.Inject

class LoadingUsecase @Inject constructor(private val loadingRepostiroy : LoadingRepository){

    suspend fun getUserProfile(context : Context) = flow{
        emit(loadingRepostiroy.getUserProfile(context))
    }

    suspend fun getDailyBoards() = flow {
        emit(loadingRepostiroy.getDailyBoards())
    }
}